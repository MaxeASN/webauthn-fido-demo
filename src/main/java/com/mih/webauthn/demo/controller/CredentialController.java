package com.mih.webauthn.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mih.webauthn.demo.controller.response.CommonResult;
import com.mih.webauthn.demo.domain.Wallet;
import com.mih.webauthn.demo.domain.dto.L1RpcParams;
import com.mih.webauthn.demo.domain.dto.L1RpcResponse;
import com.mih.webauthn.demo.exception.UserRegisterFailException;
import com.mih.webauthn.demo.service.CredentialService;
import com.mih.webauthn.demo.service.FidoService;
import com.mih.webauthn.demo.utils.AESUtils;
import com.mih.webauthn.demo.utils.CommonUtils;
import com.mih.webauthn.demo.utils.ERC4337Utils;
import com.mih.webauthn.demo.utils.ServletUtils;
import com.yubico.webauthn.RelyingParty;
import io.github.webauthn.config.WebAuthnOperation;
import io.github.webauthn.domain.*;
import io.github.webauthn.dto.RegistrationFinishRequest;
import io.github.webauthn.dto.RegistrationStartRequest;
import io.github.webauthn.dto.RegistrationStartResponse;
import io.github.webauthn.flows.InvalidTokenException;
import io.github.webauthn.flows.UsernameAlreadyExistsException;
import io.github.webauthn.flows.WebAuthnRegistrationFinishStrategy;
import io.github.webauthn.flows.WebAuthnRegistrationStartStrategy;
import io.github.webauthn.jpa.JpaWebAuthnCredentials;
import io.github.webauthn.jpa.JpaWebAuthnUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.utils.Numeric;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

@RestController
@RequestMapping("api/credential")
@CrossOrigin(origins = "http://localhost:30000", allowCredentials = "true")
public class CredentialController {
    private static final Logger log = LoggerFactory.getLogger(CredentialController.class);

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private ServletUtils servletUtils;

    @Autowired
    private ObjectMapper mapper ;

    @Autowired
    private WebAuthnUserRepository<JpaWebAuthnUser> webAuthnUserRepository;

    @Autowired
    private WebAuthnCredentialsRepository<JpaWebAuthnCredentials> webAuthnCredentialsRepository;

    @Autowired
    private RelyingParty relyingParty;

    @Autowired
    private WebAuthnOperation<RegistrationStartResponse, String> registrationOperation;

    private WebAuthnRegistrationStartStrategy startStrategy;

    private WebAuthnRegistrationFinishStrategy finishStrategy;

    private Supplier<WebAuthnUser> userSupplier = () -> {
        AbstractAuthenticationToken token = null;
        if(SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken){
            token = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        }else if(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken){
            token = (AnonymousAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        }
        if (token == null)
            return null;

        Object principal = token.getPrincipal();
        if (principal instanceof WebAuthnUser) {
            return (WebAuthnUser) principal;
        } else {
            log.warn("userSupplier - you need to configure your WebAuthnConfigurer.userSupplier method to tranform your principal implementation to something that webauthn starter can understand");
        }
        return null;
    };

    @RequestMapping("list")
    public CommonResult queryCredentials(@AuthenticationPrincipal UserDetails user){
        if(user == null){
            return CommonResult.unAuthorization();
        }
        WebAuthnUser userEntity = (WebAuthnUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> credentialList = credentialService.findAllByAppUserId(userEntity.getId());
        return CommonResult.success(credentialList);
    }

    @RequestMapping("delete")
    public CommonResult deleteCredential(@AuthenticationPrincipal UserDetails user, @RequestParam String fidoId) throws IOException {
        if(user == null){
            return CommonResult.unAuthorization();
        }
        WebAuthnUser userEntity = (WebAuthnUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        WebAuthnCredentials credential = (WebAuthnCredentials) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        if(fidoId.equals(Numeric.toHexString(credential.getPublicKeyCose()))) {
            return CommonResult.failed("无法删除当前登录设备");
        }
        credentialService.deleteCredential(userEntity.getId(), fidoId);
        return CommonResult.success();
    }

    @PostConstruct
    public void init() {
        this.startStrategy = new WebAuthnRegistrationStartStrategy(webAuthnUserRepository,
                webAuthnCredentialsRepository,
                relyingParty,
                registrationOperation);
        this.finishStrategy = new WebAuthnRegistrationFinishStrategy(webAuthnUserRepository,
                webAuthnCredentialsRepository,
                relyingParty,
                registrationOperation);
    }

    @RequestMapping("register/start")
    public void registerStart(HttpServletRequest request, HttpServletResponse response, @AuthenticationPrincipal UserDetails user) throws IOException {
        if(user == null){
            servletUtils.writeBadRequestToResponse(response, Map.of("message", "用户尚未登录"));
            return;
        }
        RegistrationStartRequest body = servletUtils.parseRequest(request, RegistrationStartRequest.class);
        try {
            Optional<WebAuthnUser> currentUser = userSupplier != null ? ofNullable(userSupplier.get()) : empty();
            RegistrationStartResponse registrationStartResponse = startStrategy.registrationStart(body, currentUser);
            //String json = mapper.writeValueAsString(registrationStartResponse);
            String json = mapper.writeValueAsString(registrationStartResponse).replace("\"id\":\"localhost\",", "");

            servletUtils.writeToResponse(response, json);
        }  catch (UsernameAlreadyExistsException var10) {
            //servletUtils.writeBadRequestToResponse(response, new RegistrationStartResponse(RegistrationStartResponse.Status.USERNAME_TAKEN));
            servletUtils.writeBadRequestToResponse(response, Map.of("message", "The username already exists!"));
        } catch (InvalidTokenException var11) {
            //servletUtils.writeBadRequestToResponse(response, new RegistrationStartResponse(RegistrationStartResponse.Status.TOKEN_INVALID));
            servletUtils.writeBadRequestToResponse(response, Map.of("message", "无效的token！"));
        }
    }

    @RequestMapping("register/finish")
    @Transactional
    public void registerFinish(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            RegistrationFinishRequest body = (RegistrationFinishRequest)servletUtils.parseRequest(request, RegistrationFinishRequest.class);
            Map<String, String> map = finishStrategy.registrationFinish(body);
            String json = this.mapper.writeValueAsString(map);
            servletUtils.writeToResponse(response, json);
        } catch (Exception e) {
            log.error("注册失败：" + e.getMessage());
            log.error(CommonUtils.toStackTrace(e));
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            servletUtils.writeBadRequestToResponse(response, Map.of("message", "注册失败，请联系管理员。"));
        }

    }
}
