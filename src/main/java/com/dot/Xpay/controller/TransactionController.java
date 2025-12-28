package com.dot.Xpay.controller;

import com.dot.Xpay.dto.response.TransactionResponse;
import com.dot.Xpay.dto.response.TransactionSummaryResponse;
import com.dot.Xpay.enums.GenerationMode;
import com.dot.Xpay.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(
            summary = "Get transactions",
            description = "Returns a paginated list of transactions with optional filtering by status, account number, and date range."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameters")
    })
    @GetMapping("")
    public ResponseEntity<Page<TransactionResponse>> getTransactions(
            @Parameter(
                    description = "Transaction status (SUCCESSFUL, FAILED, INSUFFICIENT_FUND)",
                    example = "SUCCESSFUL"
            )@RequestParam(required = false) String status,
            @Parameter(
                    description = "Account number (source or destination)",
                    example = "5300000001"
            )
            @RequestParam(required = false) String accountNumber,

            @Parameter(
                    description = "Start date (inclusive)",
                    example = "2025-01-01"
            )
            @RequestParam(required = false) LocalDate startDate,

            @Parameter(
                    description = "End date (inclusive)",
                    example = "2025-01-31"
            )
            @RequestParam(required = false) LocalDate endDate,

            @Parameter(description = "Pagination information")
            Pageable pageable
    ) {
        Page<TransactionResponse> transactions = transactionService.getTransactions(status, accountNumber, startDate, endDate, pageable);
        return ResponseEntity.ok(transactions);
    }

    @Operation(
            summary = "Get daily transaction summary",
            description = "Returns aggregated transaction statistics for a given date."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Summary retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date"),
            @ApiResponse(responseCode = "404", description = "Summary not found")
    })
    @GetMapping("/summary")
    public ResponseEntity<TransactionSummaryResponse> getTransactionSummary(
            @Parameter(
                    description = "Summary date",
                    example = "2025-01-09"
            )
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        TransactionSummaryResponse summary = transactionService.generateAndSaveSummary(date, GenerationMode.READ_ONLY);
        return ResponseEntity.ok(summary);
    }
}
