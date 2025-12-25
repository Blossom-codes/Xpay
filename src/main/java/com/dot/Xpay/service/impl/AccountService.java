package com.dot.Xpay.service.impl;

import com.dot.Xpay.domain.Account;
import com.dot.Xpay.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService implements CommandLineRunner {

    private final AccountRepository accountRepository;

    @Override
    public void run(String... args) {

        if (accountRepository.count() > 0) {
            log.info("Accounts already exist. Skipping bootstrap.");
            return;
        }

        createAccount("5300000001", new BigDecimal("100000.00"));
        createAccount("5300000002", new BigDecimal("50000.00"));
        createAccount("5300000003", new BigDecimal("25000.00"));

        log.info("Test accounts successfully created");
    }

    private void createAccount(String accountNumber, BigDecimal balance) {

        if (accountRepository.existsByAccountNumber(accountNumber)) {
            return;
        }

        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setBalance(balance);

        accountRepository.save(account);

        log.info("Created account {} with balance {}", accountNumber, balance);
    }
}
