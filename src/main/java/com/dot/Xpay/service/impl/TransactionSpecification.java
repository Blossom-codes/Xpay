package com.dot.Xpay.service.impl;

import com.dot.Xpay.domain.Transaction;
import com.dot.Xpay.enums.TransactionStatus;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TransactionSpecification {

    public static Specification<Transaction> hasStatus(TransactionStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Transaction> hasAccount(String accountNumber) {
        return (root, query, cb) -> {
            if (accountNumber == null) return null;

            return cb.or(
                    cb.equal(root.get("sourceAccount"), accountNumber),
                    cb.equal(root.get("destinationAccount"), accountNumber)
            );
        };
    }

    public static Specification<Transaction> createdBetween(
            LocalDate startDate,
            LocalDate endDate
    ) {
        return (root, query, cb) -> {
            if (startDate == null && endDate == null) return null;

            Path<LocalDateTime> createdAt = root.get("createdAt");

            if (startDate != null && endDate != null) {
                return cb.between(
                        createdAt,
                        startDate.atStartOfDay(),
                        endDate.atTime(LocalTime.MAX)
                );
            }

            if (startDate != null) {
                return cb.greaterThanOrEqualTo(
                        createdAt,
                        startDate.atStartOfDay()
                );
            }

            return cb.lessThanOrEqualTo(
                    createdAt,
                    endDate.atTime(LocalTime.MAX)
            );
        };
    }
}
