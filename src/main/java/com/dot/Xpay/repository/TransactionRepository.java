package com.dot.Xpay.repository;

import com.dot.Xpay.domain.Transaction;
import com.dot.Xpay.dto.response.TransactionSummaryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface TransactionRepository
        extends JpaRepository<Transaction, String>, JpaSpecificationExecutor<Transaction> {

    @Query("""
            SELECT new com.dot.Xpay.dto.response.TransactionSummaryResponse(
                    COUNT(t) AS totalTransactions,
                    COALESCE(SUM(t.amount),0) AS totalAmount,
                    COALESCE(SUM(t.transactionFee),0) AS totalFees,
                    COALESCE(SUM(t.commission),0) AS totalCommission,
                    '' AS date
                  ) FROM Transaction t
            WHERE t.statusCode = '00'
            AND t.status = 'SUCCESSFUL'
            AND t.createdAt BETWEEN :startDate AND :endDate
            """)
    TransactionSummaryResponse getTransactionsSummary(@Param("startDate") ZonedDateTime startDate, @Param("endDate") ZonedDateTime endDate);

    @Query(value = """
            SELECT * FROM transactions
            WHERE status = 'SUCCESSFUL'
            AND commission_processed = false
            """, nativeQuery = true)
    List<Transaction> findUnprocessedSuccessfulTransactions();
}

