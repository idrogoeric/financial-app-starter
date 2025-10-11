package com.example.financialapp.application.service;

import com.example.financialapp.application.port.in.AccountUseCase;
import com.example.financialapp.application.port.out.AppendTransactionPort;
import com.example.financialapp.application.port.out.LoadAccountPort;
import com.example.financialapp.application.port.out.SaveAccountPort;
import com.example.financialapp.domain.model.Account;
import com.example.financialapp.domain.model.Transaction;
import com.example.financialapp.infrastructure.web.dto.TransactionRes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AccountService implements AccountUseCase {
    private final LoadAccountPort loadAccountPort;
    private final SaveAccountPort saveAccountPort;
    private final AppendTransactionPort appendTransactionPort;

    public AccountService(LoadAccountPort load, SaveAccountPort save, AppendTransactionPort tx) {
        this.loadAccountPort = load;
        this.saveAccountPort = save;
        this.appendTransactionPort = tx;
    }

    @Override
    public UUID open(UUID ownerId, String type) {
        Account account = new Account(UUID.randomUUID(), ownerId, BigDecimal.ZERO);
        saveAccountPort.save(account);
        return account.getId();
    }

    @Override
    public void deposit(UUID accountId, BigDecimal amount) {
        Account acc = require(loadAccountPort.load(accountId));
        acc.credit(amount);
        saveAccountPort.save(acc);
        appendTransactionPort.append(new Transaction(UUID.randomUUID(), accountId, Transaction.Type.DEPOSIT, amount, Instant.now()));
    }

    @Override
    public void withdraw(UUID accountId, BigDecimal amount) {
        Account acc = require(loadAccountPort.load(accountId));
        acc.debit(amount);
        saveAccountPort.save(acc);
        appendTransactionPort.append(new Transaction(UUID.randomUUID(), accountId, Transaction.Type.WITHDRAWAL, amount, Instant.now()));
    }

    @Override
    public List<TransactionRes> historyOf(UUID id) {
        return null;
    }

    @Override
    public BigDecimal balanceOf(UUID id) {
        return null;
    }

    private static <T> T require(Optional<T> t) {
        return t.orElseThrow(() -> new IllegalArgumentException("Not found"));
    }
}












