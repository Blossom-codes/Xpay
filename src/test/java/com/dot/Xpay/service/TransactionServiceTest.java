package com.dot.Xpay.service;

import com.dot.Xpay.domain.Transaction;
import com.dot.Xpay.domain.TransactionDailySummary;
import com.dot.Xpay.dto.response.TransactionResponse;
import com.dot.Xpay.dto.response.TransactionSummaryResponse;
import com.dot.Xpay.enums.GenerationMode;
import com.dot.Xpay.enums.TransactionStatus;
import com.dot.Xpay.exception.CustomException;
import com.dot.Xpay.repository.TransactionDailySummaryRepository;
import com.dot.Xpay.repository.TransactionRepository;
import com.dot.Xpay.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;
    @Mock
    private TransactionDailySummaryRepository dailySummaryRepository;

    private LocalDate date;
    private TransactionDailySummary existingSummary;

    @BeforeEach
    void setUp() {
        date = LocalDate.of(2025, 12, 20);

        existingSummary = TransactionDailySummary.builder()
                .summaryDate(date)
                .totalTransactions(10L)
                .totalAmount(BigDecimal.valueOf(5000))
                .totalFees(BigDecimal.valueOf(100))
                .totalCommission(BigDecimal.valueOf(50))
                .build();
    }

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

    @Test
    void shouldReturnExistingSummaryIfAlreadyGenerated() {

        when(dailySummaryRepository.findBySummaryDate(date))
                .thenReturn(Optional.of(existingSummary));

        TransactionSummaryResponse response =
                transactionService.generateAndSaveSummary(date, GenerationMode.GENERATE_AND_PERSIST);

        assertEquals("2025-12-20", response.getDate());
        assertEquals(10L, response.getTotalTransactions());
        assertEquals(BigDecimal.valueOf(5000), response.getTotalAmount());
        assertEquals(BigDecimal.valueOf(100), response.getTotalFees());
        assertEquals(BigDecimal.valueOf(50), response.getTotalCommission());

        verify(transactionRepository, never()).getTransactionsSummary(any(), any());
        verify(dailySummaryRepository, never()).save(any());
    }

    @Test
    void shouldGenerateAndPersistSummaryWhenModeIsPersist() {

        TransactionSummaryResponse newSummary = new TransactionSummaryResponse();
        newSummary.setTotalTransactions(20L);
        newSummary.setTotalAmount(BigDecimal.valueOf(10000));
        newSummary.setTotalFees(BigDecimal.valueOf(200));
        newSummary.setTotalCommission(BigDecimal.valueOf(100));

        when(dailySummaryRepository.findBySummaryDate(date))
                .thenReturn(Optional.empty());

        when(transactionRepository.getTransactionsSummary(any(), any()))
                .thenReturn(newSummary);

        TransactionSummaryResponse response =
                transactionService.generateAndSaveSummary(date, GenerationMode.GENERATE_AND_PERSIST);

        assertEquals(20L, response.getTotalTransactions());
        assertEquals(BigDecimal.valueOf(10000), response.getTotalAmount());

        verify(transactionRepository).getTransactionsSummary(any(), any());
        verify(dailySummaryRepository).save(any(TransactionDailySummary.class));
    }

    @Test
    void shouldGenerateOnlyWhenModeIsReadOnly() {

        TransactionSummaryResponse newSummary = new TransactionSummaryResponse();
        newSummary.setTotalTransactions(20L);
        newSummary.setTotalAmount(BigDecimal.valueOf(10000));
        newSummary.setTotalFees(BigDecimal.valueOf(200));
        newSummary.setTotalCommission(BigDecimal.valueOf(100));

        when(dailySummaryRepository.findBySummaryDate(date))
                .thenReturn(Optional.empty());

        when(transactionRepository.getTransactionsSummary(any(), any()))
                .thenReturn(newSummary);

        TransactionSummaryResponse response =
                transactionService.generateAndSaveSummary(date, GenerationMode.READ_ONLY);

        assertEquals(20L, response.getTotalTransactions());
        assertEquals(BigDecimal.valueOf(10000), response.getTotalAmount());

        verify(transactionRepository).getTransactionsSummary(any(), any());
        verify(dailySummaryRepository, never()).save(any(TransactionDailySummary.class));
    }

    @Test
    void shouldIgnoreDuplicateInsertException() {

        TransactionSummaryResponse repoResponse = new TransactionSummaryResponse();
        repoResponse.setTotalTransactions(12L);
        repoResponse.setTotalAmount(BigDecimal.valueOf(6000));

        when(dailySummaryRepository.findBySummaryDate(date))
                .thenReturn(Optional.empty());

        when(transactionRepository.getTransactionsSummary(any(), any()))
                .thenReturn(repoResponse);

        doThrow(DataIntegrityViolationException.class)
                .when(dailySummaryRepository)
                .save(any());

        TransactionSummaryResponse response =
                transactionService.generateAndSaveSummary(date, GenerationMode.GENERATE_AND_PERSIST);

        assertEquals(12L, response.getTotalTransactions());

        verify(dailySummaryRepository).save(any());
    }


}
