package com.dot.Xpay.service.impl;


import com.dot.Xpay.domain.Account;
import com.dot.Xpay.domain.Transaction;
import com.dot.Xpay.dto.request.TransferRequest;
import com.dot.Xpay.dto.response.BaseResponse;
import com.dot.Xpay.dto.response.TransactionResponse;
import com.dot.Xpay.enums.TransactionStatus;
import com.dot.Xpay.exception.CustomException;
import com.dot.Xpay.repository.AccountRepository;
import com.dot.Xpay.repository.TransactionRepository;
import com.dot.Xpay.service.TransferService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.swing.text.DateFormatter;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public BaseResponse transfer(TransferRequest request) {

        String reference = UUID.randomUUID().toString().replace("-", "").toUpperCase().substring(0,16);
        String description = request.getDescription();
        try {
            Account sender = accountRepository.findByAccountNumber(request.getSourceAccount())
                    .orElseThrow(() ->
                            new CustomException("No account found with account number " + request.getSourceAccount())
                    );

            Account receiver = accountRepository.findByAccountNumber(request.getDestinationAccount())
                    .orElseThrow(() ->
                            new CustomException("No account found with account number " + request.getDestinationAccount())
                    );

            BigDecimal amount = request.getAmount();

            // Insufficient funds check
            if (sender.getBalance().compareTo(amount) < 0) {

                Transaction failedTxn = Transaction.builder()
                        .reference(reference)
                        .sourceAccount(sender.getAccountNumber())
                        .destinationAccount(receiver.getAccountNumber())
                        .amount(amount)
                        .transactionFee(BigDecimal.ZERO)
                        .billedAmount(BigDecimal.ZERO)
                        .commission(BigDecimal.ZERO)
                        .commissionWorthy(false)
                        .description(description)
                        .status(TransactionStatus.INSUFFICIENT_FUND)
                        .statusCode(TransactionStatus.INSUFFICIENT_FUND.getCode())
                        .statusMessage(TransactionStatus.INSUFFICIENT_FUND.getMessage())
                        .build();

                transactionRepository.save(failedTxn);

                return new BaseResponse(
                        TransactionStatus.INSUFFICIENT_FUND.getCode(),
                        TransactionStatus.INSUFFICIENT_FUND.getMessage(),
                        null
                );
            }

            // Calculate fee (0.5%, capped at 100)
            BigDecimal fee = amount
                    .multiply(BigDecimal.valueOf(0.005))
                    .min(BigDecimal.valueOf(100));

            BigDecimal billedAmount = amount.add(fee);

            // Update balances
            sender.setBalance(sender.getBalance().subtract(billedAmount));
            receiver.setBalance(receiver.getBalance().add(amount));

            accountRepository.save(sender);
            accountRepository.save(receiver);

            // Calculate commission
            BigDecimal commission = fee.multiply(BigDecimal.valueOf(0.20));

            // Save successful transaction
            Transaction transaction = Transaction.builder()
                    .reference(reference)
                    .sourceAccount(sender.getAccountNumber())
                    .destinationAccount(receiver.getAccountNumber())
                    .amount(amount)
                    .transactionFee(fee)
                    .billedAmount(billedAmount)
                    .commission(commission)
                    .commissionWorthy(true)
                    .description(description)
                    .status(TransactionStatus.SUCCESSFUL)
                    .statusMessage(TransactionStatus.SUCCESSFUL.getMessage())
                    .statusCode(TransactionStatus.SUCCESSFUL.getCode())
                    .build();

            TransactionResponse transactionResponse =
                    getTransactionResponse(transactionRepository.save(transaction));

            return new BaseResponse(
                    TransactionStatus.SUCCESSFUL.getCode(),
                    TransactionStatus.SUCCESSFUL.getMessage(),
                    transactionResponse
            );

        } catch (Exception ex) {
            // Handle any unexpected failure
            log.error("Transaction with ref: [{}] failed due to a system error; {}", reference, ex.getMessage());

            Transaction failedTxn = Transaction.builder()
                    .reference(reference)
                    .sourceAccount(request.getSourceAccount())
                    .destinationAccount(request.getDestinationAccount())
                    .amount(request.getAmount())
                    .transactionFee(BigDecimal.ZERO)
                    .billedAmount(BigDecimal.ZERO)
                    .description(description)
                    .commission(BigDecimal.ZERO)
                    .commissionWorthy(false)
                    .status(TransactionStatus.FAILED)
                    .statusMessage(TransactionStatus.FAILED.getMessage())
                    .statusCode(TransactionStatus.FAILED.getCode())
                    .build();

            transactionRepository.save(failedTxn);

            return new BaseResponse(
                    TransactionStatus.FAILED.getCode(),
                    TransactionStatus.FAILED.getMessage(),
                    null
            );
        }
    }

    private static TransactionResponse getTransactionResponse(Transaction saved) {
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

}
