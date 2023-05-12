package com.mih.webauthn.demo.domain.erc4337;

import java.math.BigInteger;

public class UserOperation{
    private String address = "";

    private BigInteger nonce = BigInteger.ZERO;

    private byte[] initCode = new byte[0];

    private byte[] callData = new byte[0];

    private BigInteger callGasLimit = BigInteger.ZERO;

    private BigInteger verificationGasLimit = BigInteger.ZERO;

    private BigInteger preVerificationGas = BigInteger.ZERO;

    private BigInteger maxFeePerGas = BigInteger.ZERO;

    private BigInteger maxPriorityFeePerGas = BigInteger.ZERO;

    private byte[] paymasterAndData = new byte[0];

    private byte[] fidoPublickey = new byte[0];

    private byte[] signature = new byte[0];

    public UserOperation(){

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public byte[] getFIDOPubKey() {
        return fidoPublickey;
    }

    public void setFIDOPubKey(byte[] FIDOPubKey) {
        this.fidoPublickey = FIDOPubKey;
    }

    public BigInteger getNonce() {
        return nonce;
    }

    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

    public byte[] getInitCode() {
        return initCode;
    }

    public void setInitCode(byte[] initCode) {
        this.initCode = initCode;
    }

    public byte[] getCallData() {
        return callData;
    }

    public void setCallData(byte[] callData) {
        this.callData = callData;
    }

    public BigInteger getCallGasLimit() {
        return callGasLimit;
    }

    public void setCallGasLimit(BigInteger callGasLimit) {
        this.callGasLimit = callGasLimit;
    }

    public BigInteger getVerificationGasLimit() {
        return verificationGasLimit;
    }

    public void setVerificationGasLimit(BigInteger verificationGasLimit) {
        this.verificationGasLimit = verificationGasLimit;
    }

    public BigInteger getPreVerificationGas() {
        return preVerificationGas;
    }

    public void setPreVerificationGas(BigInteger preVerificationGas) {
        this.preVerificationGas = preVerificationGas;
    }

    public BigInteger getMaxFeePerGas() {
        return maxFeePerGas;
    }

    public void setMaxFeePerGas(BigInteger maxFeePerGas) {
        this.maxFeePerGas = maxFeePerGas;
    }

    public BigInteger getMaxPriorityFeePerGas() {
        return maxPriorityFeePerGas;
    }

    public void setMaxPriorityFeePerGas(BigInteger maxPriorityFeePerGas) {
        this.maxPriorityFeePerGas = maxPriorityFeePerGas;
    }

    public byte[] getPaymasterAndData() {
        return paymasterAndData;
    }

    public void setPaymasterAndData(byte[] paymasterAndData) {
        this.paymasterAndData = paymasterAndData;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }
}
