package com.example.Wallet.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.Wallet.entity.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);

    Page<Transaction> findByFromWalletOrToWallet(
            Long fromWallet,
            Long toWallet,
            Pageable pageable
    );

}
