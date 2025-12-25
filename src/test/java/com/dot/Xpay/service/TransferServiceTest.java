package com.dot.Xpay.service;

import com.dot.Xpay.domain.Account;
import com.dot.Xpay.domain.Transaction;
import com.dot.Xpay.dto.request.TransferRequest;
import com.dot.Xpay.dto.response.BaseResponse;
import com.dot.Xpay.enums.TransactionStatus;
import com.dot.Xpay.repository.AccountRepository;
import com.dot.Xpay.repository.TransactionRepository;
import com.dot.Xpay.service.impl.TransactionServiceImpl;
import com.dot.Xpay.service.impl.TransferServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {


    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransferServiceImpl transferService;

    @Test
    void transfer() {
        Account sender = new Account("1", "53000001", BigDecimal.valueOf(2000), null, null);
        Account receiver = new Account("2", "53000002", BigDecimal.valueOf(500), null, null);

        when(accountRepository.findByAccountNumber("53000001"))
                .thenReturn(Optional.of(sender));
        when(accountRepository.findByAccountNumber("53000002"))
                .thenReturn(Optional.of(receiver));

        TransferRequest request = new TransferRequest("53000001", "53000002", BigDecimal.valueOf(1000), "Test");


        Transaction txn = new Transaction();
        txn.setAmount(request.getAmount());
        txn.setReference("Ref123");
        txn.setSourceAccount(request.getSourceAccount());
        txn.setDestinationAccount(request.getDestinationAccount());
        txn.setStatus(TransactionStatus.SUCCESSFUL);

        when(transactionRepository.save(any(Transaction.class))).thenReturn(txn);

        BaseResponse response = transferService.transfer(request);

        assertEquals(TransactionStatus.SUCCESSFUL.getCode(), response.getResponseCode());

        verify(accountRepository).save(sender);
        verify(accountRepository).save(receiver);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void shouldFailTransferWhenInsufficientFunds() {

        Account sender = new Account("1", "53000001", BigDecimal.valueOf(500), null, null);
        Account receiver = new Account("2", "53000002", BigDecimal.valueOf(1000), null, null);

        when(accountRepository.findByAccountNumber("53000001"))
                .thenReturn(Optional.of(sender));
        when(accountRepository.findByAccountNumber("53000002"))
                .thenReturn(Optional.of(receiver));

        TransferRequest request = new TransferRequest("53000001", "53000002", BigDecimal.valueOf(1000), "Test");

        BaseResponse response = transferService.transfer(request);

        assertEquals(
                TransactionStatus.INSUFFICIENT_FUND.getCode(),
                response.getResponseCode()
        );

        verify(transactionRepository).save(any(Transaction.class));
        verify(accountRepository, never()).save(any(Account.class));
    }

}