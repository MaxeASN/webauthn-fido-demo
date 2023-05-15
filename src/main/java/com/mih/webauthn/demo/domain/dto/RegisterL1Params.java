package com.mih.webauthn.demo.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@NoArgsConstructor
public class RegisterL1Params {
    @JsonProperty("jsonrpc")
    private String jsonrpc = "2.0";

    @JsonProperty("id")
    private int id = 2;

    @JsonProperty("method")
    private String method = "maxe_registL1";

    @JsonProperty("params")
    private Object[] params = new Object[2];

    public RegisterL1Params(int chainId, String accountAddress) {
        this.params[0] = chainId;
        this.params[1] = accountAddress;
    }
}
