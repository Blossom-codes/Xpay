package com.dot.Xpay.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private String reference;
    private String sourceAccount;
    private String destinationAccount;
    private BigDecimal amount;
    private BigDecimal transactionFee;
    private BigDecimal billedAmount;
    private String status;
    private String statusMessage;
    private Boolean commissionWorthy;
    private BigDecimal commission;
    private String createdAt;
}
