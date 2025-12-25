package com.dot.Xpay.controller;

import com.dot.Xpay.dto.response.TransactionResponse;
import com.dot.Xpay.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("")
    public ResponseEntity<List<TransactionResponse>> getTransactions(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String accountNumber,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<TransactionResponse> transactions = transactionService.getTransactions(status, accountNumber, startDate, endDate);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getTransactionSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
//        Map<String, Object> summary = transactionService.getSummary(date);
        return ResponseEntity.ok(null);
    }
}
