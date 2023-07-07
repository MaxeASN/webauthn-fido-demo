package com.mih.webauthn.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mih.webauthn.demo.utils.ServletUtils;
import com.yubico.webauthn.RelyingParty;
import io.github.webauthn.config.InMemoryOperation;
import io.github.webauthn.config.WebAuthnOperation;
import io.github.webauthn.config.WebAuthnUsernameAuthenticationToken;
import io.github.webauthn.domain.WebAuthnCredentials;
import io.github.webauthn.domain.WebAuthnCredentialsRepository;
import io.github.webauthn.domain.WebAuthnUser;
import io.github.webauthn.domain.WebAuthnUserRepository;
import io.github.webauthn.dto.AssertionFinishRequest;
import io.github.webauthn.dto.AssertionStartRequest;
import io.github.webauthn.dto.AssertionStartResponse;
import io.github.webauthn.flows.WebAuthnAssertionFinishStrategy;
import io.github.webauthn.flows.WebAuthnAssertionStartStrategy;
import io.github.webauthn.jpa.JpaWebAuthnCredentials;
import io.github.webauthn.jpa.JpaWebAuthnUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

@RestController
@RequestMapping("api/diyLogin")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class LoginController {

    @Autowired
    private ServletUtils servletUtils;

    @Autowired
    private ObjectMapper mapper ;

    @Autowired
    private RelyingParty relyingParty;

    private final WebAuthnOperation<AssertionStartResponse, String> assertionOperation = new InMemoryOperation<>();

    @Autowired
    private WebAuthnUserRepository<JpaWebAuthnUser> webAuthnUserRepository;

    @Autowired
    private WebAuthnCredentialsRepository<JpaWebAuthnCredentials> webAuthnCredentialsRepository;

    private WebAuthnAssertionStartStrategy assertionStartStrategy;

    private WebAuthnAssertionFinishStrategy assertionFinishStrategy;

    private final BiConsumer<WebAuthnUser, WebAuthnCredentials> successHandler = (user, credentials) -> {
        UsernamePasswordAuthenticationToken token = new WebAuthnUsernameAuthenticationToken(user, credentials, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(token);
    };

    @PostConstruct
    public void init() {
        this.assertionStartStrategy = new WebAuthnAssertionStartStrategy(relyingParty, assertionOperation);
        this.assertionFinishStrategy = new WebAuthnAssertionFinishStrategy(webAuthnUserRepository, webAuthnCredentialsRepository, relyingParty, assertionOperation);
    }

    @RequestMapping("start")
    public void loginStart(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AssertionStartRequest startRequest = servletUtils.parseRequest(request, AssertionStartRequest.class);

        try {
            AssertionStartResponse start = this.assertionStartStrategy.start(startRequest);
            String json = this.mapper.writeValueAsString(start);
            servletUtils.writeToResponse(response, json);
        } catch (UsernameNotFoundException var9) {
            servletUtils.writeBadRequestToResponse(response, Map.of("message", var9.getMessage()));
        }
    }

    @RequestMapping("finish")
    public void loginFinish(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AssertionFinishRequest body = servletUtils.parseRequest(request, AssertionFinishRequest.class);
        Optional<WebAuthnAssertionFinishStrategy.AssertionSuccessResponse> res = this.assertionFinishStrategy.finish(body);
        res.ifPresent((u) -> {
            this.successHandler.accept(u.getUser(), u.getCredentials());
        });
        servletUtils.writeToResponse(response, this.mapper.writeValueAsString(Map.of("username", ((WebAuthnAssertionFinishStrategy.AssertionSuccessResponse)res.get()).getUser().getUsername())));
    }
}
