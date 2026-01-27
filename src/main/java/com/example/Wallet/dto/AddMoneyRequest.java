package com.example.Wallet.dto;

import java.math.BigDecimal;

public class AddMoneyRequest {

    private BigDecimal amount;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
