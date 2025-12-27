package com.dot.Xpay.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Entity
@Table(
        name = "transaction_daily_summary",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "summary_date")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDailySummary {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "summary_date", nullable = false)
    private LocalDate summaryDate;

    @Column(nullable = false)
    private Long totalTransactions;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalFees;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalCommission;

    @CreationTimestamp
    private ZonedDateTime createdAt;
}
