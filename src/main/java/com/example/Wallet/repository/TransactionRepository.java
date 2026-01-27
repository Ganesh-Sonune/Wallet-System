package com.example.Wallet.repository;

import com.example.Wallet.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // 🔑 For idempotency (ADD & TRANSFER)
    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);

    // 📜 For transaction history
    Page<Transaction> findByFromWalletOrToWallet(
            Long fromWallet,
            Long toWallet,
            Pageable pageable
    );
}
