package com.mih.webauthn.demo.controller;

import com.mih.webauthn.demo.controller.response.CommonResult;
import com.mih.webauthn.demo.domain.UserFaceDataRepo;
import com.mih.webauthn.demo.service.FaceDataService;
import io.github.webauthn.domain.WebAuthnUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("api/faceData")
@CrossOrigin(origins = "http://localhost:30000", allowCredentials = "true")
public class FaceDataController {
    @Autowired
    private FaceDataService faceDataService;

    @RequestMapping(value = "importFace",method = RequestMethod.POST)
    public CommonResult importFaceData(@RequestParam String faceData, @AuthenticationPrincipal UserDetails user){
        System.out.println(faceData.length());
        System.out.println(faceData);
        if(user == null){
            return CommonResult.unAuthorization();
        }
        boolean isFloatArray = Arrays.stream(faceData.split(",")).allMatch((String floatStr) -> {
            try{
                Float.parseFloat(floatStr);
            }catch (Exception e){
                return false;
            }
            return true;
        });
        if(!isFloatArray){
            return CommonResult.failed("脸部数据格式错误");
        }
        WebAuthnUser userEntity = (WebAuthnUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        faceDataService.importFaceDate(userEntity.getId(), faceData);
        return CommonResult.success();
    }

    @RequestMapping("queryAllFaceData")
    public CommonResult queryAllFaceData(@AuthenticationPrincipal UserDetails user, @RequestParam String apiKey) {
        if(user == null){
            return CommonResult.unAuthorization();
        }
        if(!apiKey.equals("")){
            return CommonResult.failed("apiKey有误");
        }
        return CommonResult.success(faceDataService.queryAllFaceData());
    }
}
