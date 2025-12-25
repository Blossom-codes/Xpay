package com.dot.Xpay.domain;

import com.dot.Xpay.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(
        name = "transactions",
        indexes = {
                @Index(name = "idx_tx_status", columnList = "status"),
                @Index(name = "idx_tx_created_at", columnList = "created_at"),
                @Index(name = "idx_tx_source_account", columnList = "source_account"),
                @Index(name = "idx_tx_destination_account", columnList = "destination_account")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, length = 50)
    private String reference;

    @Column(name = "source_account", nullable = false, length = 20)
    private String sourceAccount;

    @Column(name = "destination_account", nullable = false, length = 20)
    private String destinationAccount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "transaction_fee", nullable = false, precision = 19, scale = 2)
    private BigDecimal transactionFee;

    @Column(name = "billed_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal billedAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TransactionStatus status;

    @Column(name = "status_message")
    private String statusMessage;

    @Column(name = "commission_worthy")
    private Boolean commissionWorthy = false;

    @Column(precision = 19, scale = 2)
    private BigDecimal commission;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;
}
