package com.example.Wallet.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "transactions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "idempotency_key")
        }
)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * NULL for ADD operation
     */
    @Column(name = "from_wallet")
    private Long fromWallet;

    /**
     * Target wallet (for ADD and TRANSFER)
     */
    @Column(name = "to_wallet")
    private Long toWallet;

    @Column(nullable = false)
    private BigDecimal amount;

    /**
     * ADD, TRANSFER
     */
    @Column(nullable = false)
    private String type;

    /**
     * SUCCESS, FAILED
     */
    @Column(nullable = false)
    private String status;

    /**
     * Enforces idempotency at DB level
     */
    @Column(name = "idempotency_key", nullable = false)
    private String idempotencyKey;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // ===== getters & setters =====

    public Long getId() {
        return id;
    }

    public Long getFromWallet() {
        return fromWallet;
    }

    public void setFromWallet(Long fromWallet) {
        this.fromWallet = fromWallet;
    }

    public Long getToWallet() {
        return toWallet;
    }

    public void setToWallet(Long toWallet) {
        this.toWallet = toWallet;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
