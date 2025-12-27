package com.dot.Xpay.service;


import com.dot.Xpay.dto.response.TransactionResponse;
import com.dot.Xpay.dto.response.TransactionSummaryResponse;
import com.dot.Xpay.enums.GenerationMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface TransactionService {

    Page<TransactionResponse> getTransactions(String status, String accountNumber, LocalDate startDate, LocalDate endDate, Pageable pageable);
    TransactionSummaryResponse generateAndSaveSummary(LocalDate date, GenerationMode mode);
}
