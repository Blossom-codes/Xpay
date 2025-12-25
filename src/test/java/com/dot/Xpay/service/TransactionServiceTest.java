package com.dot.Xpay.service;

import com.dot.Xpay.domain.Transaction;
import com.dot.Xpay.dto.response.TransactionResponse;
import com.dot.Xpay.enums.TransactionStatus;
import com.dot.Xpay.exception.CustomException;
import com.dot.Xpay.repository.TransactionRepository;
import com.dot.Xpay.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void shouldReturnTransactionsWithFilters() {

        // given
        Transaction txn = Transaction.builder()
                .reference("REF123")
                .sourceAccount("1000000001")
                .destinationAccount("1000000002")
                .amount(BigDecimal.valueOf(1000))
                .status(TransactionStatus.SUCCESSFUL)
                .build();

        Pageable pageable = PageRequest.of(0, 10);

        when(transactionRepository.findAll(
                ArgumentMatchers.<Specification<Transaction>>any(),
                eq(pageable)
        )).thenReturn(new PageImpl<>(List.of(txn)));


        // when
        Page<TransactionResponse> result =
                transactionService.getTransactions(
                        "SUCCESSFUL",
                        "1000000001",
                        LocalDate.now().minusDays(1),
                        LocalDate.now(),
                        pageable
                );

        // then
        assertEquals(1, result.getTotalElements());
        assertEquals("REF123", result.getContent().get(0).getReference());


        verify(transactionRepository).findAll(ArgumentMatchers.<Specification<Transaction>>any(), eq(pageable));
    }

    @Test
    void shouldThrowExceptionForInvalidStatus() {

        assertThrows(CustomException.class, () ->
                transactionService.getTransactions(
                        "INVALID_STATUS",
                        null,
                        null,
                        null,
                        PageRequest.of(0, 10)
                )
        );
    }

    @Test
    void shouldFailWhenStartDateAfterEndDate() {

        LocalDate start = LocalDate.now();
        LocalDate end = start.minusDays(1);

        assertThrows(CustomException.class, () ->
                transactionService.getTransactions(
                        null,
                        null,
                        start,
                        end,
                        PageRequest.of(0, 10)
                )
        );
    }


}
