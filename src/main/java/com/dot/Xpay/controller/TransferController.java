package com.dot.Xpay.controller;

import com.dot.Xpay.dto.request.TransferRequest;
import com.dot.Xpay.dto.response.BaseResponse;
import com.dot.Xpay.dto.response.TransactionResponse;
import com.dot.Xpay.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @Operation(
            summary = "Transfer funds between accounts",
            description = "Initiates a money transfer, applies transaction fees, and updates account balances."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction successful"),
            @ApiResponse(responseCode = "400", description = "Invalid request or insufficient balance"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("")
    public ResponseEntity<BaseResponse> createTransfer(
            @Valid @RequestBody TransferRequest request) {
        BaseResponse response = transferService.transfer(request);
        return ResponseEntity.ok(response);
    }


}
