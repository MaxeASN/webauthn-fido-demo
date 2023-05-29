package com.mih.webauthn.demo;

import com.mih.webauthn.demo.domain.Account;
import com.mih.webauthn.demo.domain.AccountRepo;
import com.mih.webauthn.demo.domain.Wallet;
import com.mih.webauthn.demo.domain.WalletRepo;
import io.github.webauthn.domain.WebAuthnCredentialsRepository;
import io.github.webauthn.domain.WebAuthnUserRepository;
import io.github.webauthn.jpa.JpaWebAuthnCredentials;
import io.github.webauthn.jpa.JpaWebAuthnUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public String accounts(Model model, @AuthenticationPrincipal UserDetails user) {
        JpaWebAuthnUser jpaWebAuthnUser = webAuthnUserRepository.findByUsername(user.getUsername()).get();
        Wallet wallet = walletRepo.findByAppUserId(jpaWebAuthnUser.getId()).iterator().next();
        //TODO：显示自己登录的那个fidoPublicKey
        JpaWebAuthnCredentials credential = webAuthnCredentialsRepository.findAllByAppUserId(jpaWebAuthnUser.getId()).get(0);
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
