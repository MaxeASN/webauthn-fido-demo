package com.mih.webauthn.demo.controller;

import com.mih.webauthn.demo.controller.response.CommonResult;
import com.mih.webauthn.demo.domain.L1Address;
import com.mih.webauthn.demo.domain.L1AddressRepo;
import com.mih.webauthn.demo.domain.Wallet;
import com.mih.webauthn.demo.domain.WalletRepo;
import io.github.webauthn.domain.WebAuthnUserRepository;
import io.github.webauthn.jpa.JpaWebAuthnUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/l1")
@CrossOrigin
public class L1Controller {
    @Autowired
    private L1AddressRepo l1AddressRepo;

    @Autowired
    private WebAuthnUserRepository<JpaWebAuthnUser> webAuthnUserRepository;

    @RequestMapping("list")
    public CommonResult queryL1Address(@AuthenticationPrincipal UserDetails user){
        if(user == null){
            return CommonResult.failed("用户尚未登录");
        }
        JpaWebAuthnUser userEntity = webAuthnUserRepository.findByUsername(user.getUsername()).get();
//        JpaWebAuthnUser userEntity = webAuthnUserRepository.findByUsername("chen10").get();
        List<L1Address> entityList = l1AddressRepo.findAllByAppUserId(userEntity.getId());
        List<String> l1AddressList = entityList.stream().map(L1Address -> L1Address.getL1Address().toLowerCase()).collect(Collectors.toList());
        return CommonResult.success(l1AddressList);
    }
}
