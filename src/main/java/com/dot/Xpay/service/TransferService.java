package com.dot.Xpay.service;

import com.dot.Xpay.dto.request.TransferRequest;
import com.dot.Xpay.dto.response.BaseResponse;

public interface TransferService {

    BaseResponse transfer(TransferRequest request);
}