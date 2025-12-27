package com.dot.Xpay.repository;

import com.dot.Xpay.domain.TransactionDailySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface TransactionDailySummaryRepository
        extends JpaRepository<TransactionDailySummary, String> {

    Optional<TransactionDailySummary> findBySummaryDate(LocalDate summaryDate);
}

