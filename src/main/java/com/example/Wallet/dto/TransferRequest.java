package com.example.Wallet.dto;

import java.math.BigDecimal;

public class TransferRequest {

    private Long toWalletId;
    private BigDecimal amount;

    public Long getToWalletId() {
        return toWalletId;
    }

    public void setToWalletId(Long toWalletId) {
        this.toWalletId = toWalletId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
