package com.mih.webauthn.demo.service.impl;

import com.github.pagehelper.PageHelper;
import com.mih.webauthn.demo.constant.TransactionConst;
import com.mih.webauthn.demo.controller.response.CommonResult;
import com.mih.webauthn.demo.domain.L1Address;
import com.mih.webauthn.demo.domain.L1AddressRepo;
import com.mih.webauthn.demo.domain.Transaction;
import com.mih.webauthn.demo.domain.TransactionRepo;
import com.mih.webauthn.demo.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private L1AddressRepo l1AddressRepo;

    @Override
    public List<Transaction> findAllByAppUserId(long appUserId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return transactionRepo.findAllByAppUserId(appUserId);
    }

    @Override
    public Long findL1AddressIdByAddress(String fromAddress) {
        L1Address entity = l1AddressRepo.findByL1Address(fromAddress);
        return entity == null ? null : entity.getId();
    }

    @Override
    public boolean verifyFromAddress(String fromAddress, Long userId) {
        L1Address entity = l1AddressRepo.findByL1Address(fromAddress);
        return Objects.equals(entity.getAppUserId(), userId);
    }
}
