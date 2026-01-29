package com.example.Wallet.service;

import com.example.Wallet.entity.Transaction;
import com.example.Wallet.entity.Wallet;
import com.example.Wallet.repository.TransactionRepository;
import com.example.Wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public AdminService(WalletRepository walletRepository,
                        TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<Wallet> getAllWallets() {
        return walletRepository.findAll();
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
