package com.example.Wallet.controller;

import com.example.Wallet.entity.Transaction;
import com.example.Wallet.entity.Wallet;
import com.example.Wallet.service.AdminService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/wallets")
    public List<Wallet> getAllWallets() {
        return adminService.getAllWallets();
    }

    @GetMapping("/transactions")
    public List<Transaction> getAllTransactions() {
        return adminService.getAllTransactions();
    }
}
