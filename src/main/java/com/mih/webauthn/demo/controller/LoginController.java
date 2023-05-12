package com.mih.webauthn.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mih.webauthn.demo.utils.ServletUtils;
import com.yubico.webauthn.RelyingParty;
import io.github.webauthn.config.WebAuthnOperation;
import io.github.webauthn.dto.AssertionFinishRequest;
import io.github.webauthn.dto.AssertionStartRequest;
import io.github.webauthn.dto.AssertionStartResponse;
import io.github.webauthn.flows.WebAuthnAssertionFinishStrategy;
import io.github.webauthn.flows.WebAuthnAssertionStartStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

//@RestController
//@RequestMapping("api/diyLogin")
public class LoginController {

//    @Autowired
//    private ServletUtils servletUtils;
//
//    @Autowired
//    private ObjectMapper mapper ;
//
//    @Autowired
//    private RelyingParty relyingParty;
//
//    @Autowired
//    private WebAuthnOperation<AssertionStartResponse, String> operation;
//
//    private WebAuthnAssertionStartStrategy assertionStartStrategy;
//
//    @PostConstruct
//    public void init() {
//        this.assertionStartStrategy = new WebAuthnAssertionStartStrategy(relyingParty, operation);
//    }
//
//    @RequestMapping("start")
//    public void loginStart(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        AssertionStartRequest startRequest = servletUtils.parseRequest(request, AssertionStartRequest.class);
//
//        try {
//            AssertionStartResponse start = this.assertionStartStrategy.start(startRequest);
//            String json = this.mapper.writeValueAsString(start);
//            servletUtils.writeToResponse(response, json);
//        } catch (UsernameNotFoundException var9) {
//            servletUtils.writeBadRequestToResponse(response, Map.of("message", var9.getMessage()));
//        }
//    }
//
//    @RequestMapping("finish")
//    public void loginFinish(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        AssertionFinishRequest body = servletUtils.parseRequest(request, AssertionFinishRequest.class);
//        Optional res = this.assertionFinishStrategy.finish(body);
//        res.ifPresent((u) -> {
//            this.successHandler.accept(u.getUser(), u.getCredentials());
//        });
//        this.writeToResponse(response, this.mapper.writeValueAsString(Map.of("username", ((WebAuthnAssertionFinishStrategy.AssertionSuccessResponse)res.get()).getUser().getUsername())));
//    }
}
