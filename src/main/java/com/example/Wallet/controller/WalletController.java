package com.example.Wallet.controller;

import com.example.Wallet.dto.*;

import com.example.Wallet.service.WalletService;
import org.springframework.web.bind.annotation.*;


import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping
    public WalletResponse getWallet() {
        return walletService.getMyWallet();
    }

    @PostMapping("/add")
    public WalletResponse addMoney(
            @RequestBody AddMoneyRequest request,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {

        return walletService.addMoney(request, idempotencyKey);
    }

    @PostMapping("/transfer")
    public TransferResponse transfer(
            @RequestBody TransferRequest request,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {

        return walletService.transferMoney(request, idempotencyKey);
    }

    @GetMapping("/transactions")
    public Page<TransactionResponse> getTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return walletService.getMyTransactions(page, size);
    }



}
