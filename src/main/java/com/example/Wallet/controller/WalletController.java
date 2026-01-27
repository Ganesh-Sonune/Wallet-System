package com.example.Wallet.controller;

import com.example.Wallet.dto.AddMoneyRequest;
import com.example.Wallet.dto.TransferRequest;
import com.example.Wallet.dto.WalletResponse;
import com.example.Wallet.entity.Wallet;
import com.example.Wallet.service.WalletService;
import org.springframework.web.bind.annotation.*;

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
    public void transfer(
            @RequestBody TransferRequest request,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {

        walletService.transferMoney(request, idempotencyKey);
    }



}
