package com.example.financialapp.application.port.in;

import com.example.financialapp.infrastructure.web.dto.TransactionRes;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface AccountUseCase {
    UUID open(UUID ownerId, String type);
    void deposit(UUID accountId, BigDecimal amount);
    void withdraw(UUID accountId, BigDecimal amount);
    List<TransactionRes> historyOf(UUID accountId);
    BigDecimal balanceOf(UUID accountId);
}








