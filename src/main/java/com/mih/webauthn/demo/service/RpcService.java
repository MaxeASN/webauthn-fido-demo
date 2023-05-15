package com.mih.webauthn.demo.service;

import com.mih.webauthn.demo.domain.dto.RegisterL1Params;
import com.mih.webauthn.demo.domain.dto.RegisterL1Response;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "rpcService", url = "http://192.168.0.176:27890")
@Component
public interface RpcService {
    @RequestLine("POST /")
    RegisterL1Response maxeRegistL1(@RequestBody RegisterL1Params params);
}
