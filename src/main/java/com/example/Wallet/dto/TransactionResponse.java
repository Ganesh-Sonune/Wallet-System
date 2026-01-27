package com.example.Wallet.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {


        private Long id;
        private String type;
        private String status;
        private BigDecimal amount;
        private Long fromWallet;
        private Long toWallet;
        private LocalDateTime createdAt;

        public TransactionResponse(
                Long id,
                String type,
                String status,
                BigDecimal amount,
                Long fromWallet,
                Long toWallet,
                LocalDateTime createdAt
        ) {
            this.id = id;
            this.type = type;
            this.status = status;
            this.amount = amount;
            this.fromWallet = fromWallet;
            this.toWallet = toWallet;
            this.createdAt = createdAt;
        }

        public Long getId() { return id; }
        public String getType() { return type; }
        public String getStatus() { return status; }
        public BigDecimal getAmount() { return amount; }
        public Long getFromWallet() { return fromWallet; }
        public Long getToWallet() { return toWallet; }
        public LocalDateTime getCreatedAt() { return createdAt; }


}
