package com.dot.Xpay.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {
    @NotBlank
    private String sourceAccount;

    @NotBlank
    private String destinationAccount;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    private String description;
}
