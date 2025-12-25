package com.dot.Xpay.service;


import com.dot.Xpay.dto.response.BaseResponse;
import com.dot.Xpay.dto.response.TransactionResponse;

import java.time.LocalDate;
import java.util.List;

public interface TransactionService {

    List<TransactionResponse> getTransactions(String status, String accountNumber, LocalDate startDate, LocalDate endDate);
}
