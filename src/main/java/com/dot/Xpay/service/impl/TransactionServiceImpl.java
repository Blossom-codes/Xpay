package com.dot.Xpay.service.impl;


import com.dot.Xpay.dto.response.TransactionResponse;
import com.dot.Xpay.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {


    @Override
    public List<TransactionResponse> getTransactions(String status, String accountNumber, LocalDate startDate, LocalDate endDate) {

        return List.of();
    }
}