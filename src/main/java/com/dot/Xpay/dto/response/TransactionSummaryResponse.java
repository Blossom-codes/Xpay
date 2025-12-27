package com.dot.Xpay.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionSummaryResponse {
    private long totalTransactions;
    private BigDecimal totalAmount;
    private BigDecimal totalFees;
    private BigDecimal totalCommission;
    private String date;
}
