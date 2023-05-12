package com.mih.webauthn.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mih.webauthn.demo.domain.Wallet;
import com.mih.webauthn.demo.service.FidoService;
import com.mih.webauthn.demo.utils.ERC4337Utils;
import com.mih.webauthn.demo.utils.ServletUtils;
import com.yubico.webauthn.RelyingParty;
import io.github.webauthn.config.WebAuthnOperation;
import io.github.webauthn.domain.WebAuthnCredentialsRepository;
import io.github.webauthn.domain.WebAuthnUser;
import io.github.webauthn.domain.WebAuthnUserRepository;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

@RestController
@RequestMapping("api/diyRegister")
public class RegisterController {
    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

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

    @Autowired
    private ERC4337Utils erc4337Utils;

    @Autowired
    private FidoService fidoService;

    private Supplier<WebAuthnUser> userSupplier = () -> {
        AbstractAuthenticationToken token = null;
        if(SecurityContextHolder.getContext().getAuthentication() instanceof  UsernamePasswordAuthenticationToken){
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

    @RequestMapping("start")
    public void registerStart(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //TODO:当用户登录成功后，再去注册时会将设备注册进登录的账户下
        RegistrationStartRequest body = servletUtils.parseRequest(request, RegistrationStartRequest.class);
        WebAuthnRegistrationStartStrategy startStrategy = new WebAuthnRegistrationStartStrategy(webAuthnUserRepository,
                webAuthnCredentialsRepository,
                relyingParty,
                registrationOperation);
        try {
            Optional<WebAuthnUser> currentUser = userSupplier != null ? ofNullable(userSupplier.get()) : empty();
            RegistrationStartResponse registrationStartResponse = startStrategy.registrationStart(body, currentUser);
            String json = mapper.writeValueAsString(registrationStartResponse);

            servletUtils.writeToResponse(response, json);
        }  catch (UsernameAlreadyExistsException var10) {
            servletUtils.writeBadRequestToResponse(response, new RegistrationStartResponse(RegistrationStartResponse.Status.USERNAME_TAKEN));
        } catch (InvalidTokenException var11) {
            servletUtils.writeBadRequestToResponse(response, new RegistrationStartResponse(RegistrationStartResponse.Status.TOKEN_INVALID));
        }
    }

    @RequestMapping("finish")
    public void registerFinish(HttpServletRequest request, HttpServletResponse response) throws IOException {
        WebAuthnRegistrationFinishStrategy finishStrategy = new WebAuthnRegistrationFinishStrategy(webAuthnUserRepository,
                webAuthnCredentialsRepository,
                relyingParty,
                registrationOperation);
        RegistrationFinishRequest body = (RegistrationFinishRequest)servletUtils.parseRequest(request, RegistrationFinishRequest.class);
        Map<String, String> map = finishStrategy.registrationFinish(body);
        String json = this.mapper.writeValueAsString(map);
        //给用户生成一个以太坊私钥,并导出地址存进数据库
        byte[] credentialId = body.getCredential().getId().getBytes();
        JpaWebAuthnCredentials credential = webAuthnCredentialsRepository.findByCredentialId(credentialId).get(0);
        Wallet wallet = fidoService.registerAddressAndPrivateKey(credential.getAppUserId());

        //调合约注册
        erc4337Utils.registerSoulAccount(wallet, credential.getPublicKeyCose());

        servletUtils.writeToResponse(response, json);

    }
}
