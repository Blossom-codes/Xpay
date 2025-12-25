package com.dot.Xpay.service;


import com.dot.Xpay.dto.response.BaseResponse;
import com.dot.Xpay.dto.response.TransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface TransactionService {

    Page<TransactionResponse> getTransactions(String status, String accountNumber, LocalDate startDate, LocalDate endDate, Pageable pageable);
}
