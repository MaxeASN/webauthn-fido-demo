package com.mih.webauthn.demo.service.impl;

import com.github.pagehelper.PageHelper;
import com.mih.webauthn.demo.constant.TransactionConst;
import com.mih.webauthn.demo.controller.response.CommonResult;
import com.mih.webauthn.demo.domain.Transaction;
import com.mih.webauthn.demo.domain.TransactionRepo;
import com.mih.webauthn.demo.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionRepo transactionRepo;

    @Override
    public List<Transaction> findAllByAppUserId(long appUserId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return transactionRepo.findAllByAppUserId(appUserId);
    }
}
