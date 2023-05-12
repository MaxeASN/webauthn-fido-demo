package com.mih.webauthn.demo.service;

import com.mih.webauthn.demo.domain.Transaction;

import javax.persistence.OrderBy;
import java.util.List;

public interface TransactionService {

    List<Transaction> findAllByAppUserId(long appUserId, int pageNum, int pageSize);

}
