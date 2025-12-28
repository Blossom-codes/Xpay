package com.dot.Xpay.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Fund transfer request payload")
public class TransferRequest {

    @Schema(example = "5300000001")
    @NotBlank
    private String sourceAccount;

    @Schema(example = "5300000002")
    @NotBlank
    private String destinationAccount;

    @Schema(example = "1000.00")
    @DecimalMin("0.01")
    private BigDecimal amount;

    @Schema(example = "Wallet funding")
    private String description;
}
