package com.mih.webauthn.demo;

import com.mih.webauthn.demo.domain.Account;
import com.mih.webauthn.demo.domain.AccountRepo;
import com.mih.webauthn.demo.domain.Wallet;
import com.mih.webauthn.demo.domain.WalletRepo;
import io.github.webauthn.domain.WebAuthnCredentials;
import io.github.webauthn.domain.WebAuthnCredentialsRepository;
import io.github.webauthn.domain.WebAuthnUser;
import io.github.webauthn.domain.WebAuthnUserRepository;
import io.github.webauthn.jpa.JpaWebAuthnCredentials;
import io.github.webauthn.jpa.JpaWebAuthnUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.web3j.utils.Numeric;

import java.util.Map;

@Controller
@CrossOrigin
public class HomeController {

    @Autowired
    private WebAuthnUserRepository<JpaWebAuthnUser> webAuthnUserRepository;

    @Autowired
    private WebAuthnCredentialsRepository<JpaWebAuthnCredentials> webAuthnCredentialsRepository;

    @Autowired
    WalletRepo walletRepo;

    @Autowired
    UserDetailsService userDetailsService;

    @GetMapping(value = {"/", "/home"})
    public String accounts(Model model) {
        WebAuthnCredentials credential = (WebAuthnCredentials) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        WebAuthnUser user = (WebAuthnUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Wallet wallet = walletRepo.findByAppUserId(user.getId()).iterator().next();
        model.addAttribute("username", user.getUsername());
        model.addAttribute("address", wallet.getAddress());
        model.addAttribute("fidoPublicKey", Numeric.toHexString(credential.getPublicKeyCose()));
        return "home";
    }

    @GetMapping("/send")
    public String send(){
        return "send";
    }
}
