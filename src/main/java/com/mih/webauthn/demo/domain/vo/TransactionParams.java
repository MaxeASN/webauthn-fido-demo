package com.mih.webauthn.demo.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionParams {
    private int coin;

    private String toAddress;

    private BigDecimal amount;
}
