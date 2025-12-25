package com.dot.Xpay.controller;

import com.dot.Xpay.dto.request.TransferRequest;
import com.dot.Xpay.dto.response.BaseResponse;
import com.dot.Xpay.dto.response.TransactionResponse;
import com.dot.Xpay.service.TransferService;
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

    @PostMapping("")
    public ResponseEntity<BaseResponse> createTransfer(
            @Valid @RequestBody TransferRequest request) {
        BaseResponse response = transferService.transfer(request);
        return ResponseEntity.ok(response);
    }


}
