package com.dot.Xpay.service.impl;


import com.dot.Xpay.domain.Transaction;
import com.dot.Xpay.dto.response.TransactionResponse;
import com.dot.Xpay.enums.TransactionStatus;
import com.dot.Xpay.exception.CustomException;
import com.dot.Xpay.repository.TransactionRepository;
import com.dot.Xpay.service.TransactionService;
import jakarta.persistence.criteria.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {


    private final TransactionRepository transactionRepository;

    @Override
    public Page<TransactionResponse> getTransactions(
            String status,
            String accountNumber,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {

        validateDateRange(startDate, endDate);

        TransactionStatus txnStatus =
                status != null ? TransactionStatus.valueOf(status.toUpperCase()) : null;

        Specification<Transaction> spec = Specification
                .where(TransactionSpecification.hasStatus(txnStatus))
                .and(TransactionSpecification.hasAccount(accountNumber))
                .and(TransactionSpecification.createdBetween(startDate, endDate));

        return transactionRepository.findAll(spec, pageable)
                .map(this::getTransactionResponse);
    }

    public TransactionResponse getTransactionResponse(Transaction saved) {
        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setReference(saved.getReference());
        transactionResponse.setSourceAccount(saved.getSourceAccount());
        transactionResponse.setDestinationAccount(saved.getDestinationAccount());
        transactionResponse.setAmount(saved.getAmount());
        transactionResponse.setTransactionFee(saved.getTransactionFee());
        transactionResponse.setBilledAmount(saved.getBilledAmount());
        transactionResponse.setStatus(saved.getStatus().name());
        transactionResponse.setStatusMessage(saved.getStatus().getMessage());
        transactionResponse.setCommissionWorthy(saved.getCommissionWorthy());
        transactionResponse.setCommission(saved.getCommission());
        transactionResponse.setDescription(saved.getDescription());
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        transactionResponse.setCreatedAt(saved.getCreatedAt() != null ? saved.getCreatedAt().format(dateFormatter) : ZonedDateTime.now().format(dateFormatter));
        return transactionResponse;
    }
    private void validateDateRange(LocalDate startDate, LocalDate endDate) {

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new CustomException("Start date cannot be after end date");
        }
    }

}

