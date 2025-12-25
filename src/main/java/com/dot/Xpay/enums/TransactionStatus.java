package com.dot.Xpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionStatus {
    SUCCESSFUL("00","Transaction Successful"),
    INSUFFICIENT_FUND("02","Insufficient Account Balance"),
    FAILED("99","Transaction Failed");

    public final String code;
    public final String message;
}
