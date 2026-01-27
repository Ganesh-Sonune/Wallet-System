package com.example.Wallet.dto;

import java.math.BigDecimal;

public class TransferResponse {

    private String message;
    private BigDecimal fromWalletBalance;
    private BigDecimal toWalletBalance;

    public TransferResponse(String message,
                            BigDecimal fromWalletBalance,
                            BigDecimal toWalletBalance) {
        this.message = message;
        this.fromWalletBalance = fromWalletBalance;
        this.toWalletBalance = toWalletBalance;
    }

    public String getMessage() {
        return message;
    }

    public BigDecimal getFromWalletBalance() {
        return fromWalletBalance;
    }

    public BigDecimal getToWalletBalance() {
        return toWalletBalance;
    }
}
