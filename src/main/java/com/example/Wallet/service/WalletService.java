package com.example.Wallet.service;

import com.example.Wallet.dto.AddMoneyRequest;
import com.example.Wallet.dto.TransferRequest;
import com.example.Wallet.dto.WalletResponse;
import com.example.Wallet.entity.Transaction;
import com.example.Wallet.entity.User;
import com.example.Wallet.entity.Wallet;
import com.example.Wallet.exception.DuplicateRequestException;
import com.example.Wallet.repository.TransactionRepository;
import com.example.Wallet.repository.UserRepository;
import com.example.Wallet.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    private final TransactionRepository transactionRepository;

    public WalletService(UserRepository userRepository,
                         WalletRepository walletRepository,
                         TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }


    public WalletResponse getMyWallet() {

        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        return new WalletResponse(wallet.getId(), wallet.getBalance());
    }

    @Transactional
    public WalletResponse addMoney(AddMoneyRequest request, String idempotencyKey) {

        // 1. Idempotency check
        transactionRepository.findByIdempotencyKey(idempotencyKey)
                .ifPresent(tx -> {
                    throw new RuntimeException("Duplicate request");
                });

        // 2. Get authenticated user
        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        // 3. Update balance
        wallet.setBalance(wallet.getBalance().add(request.getAmount()));

        // 4. Save transaction
        Transaction tx = new Transaction();
        tx.setToWallet(wallet.getId());
        tx.setAmount(request.getAmount());
        tx.setType("ADD");
        tx.setStatus("SUCCESS");
        tx.setIdempotencyKey(idempotencyKey);

        transactionRepository.save(tx);

        // 5. Save wallet (optimistic lock happens here)
        Wallet updatedWallet = walletRepository.save(wallet);

        return new WalletResponse(updatedWallet.getId(), updatedWallet.getBalance());
    }

    @Transactional
    public void transferMoney(TransferRequest request, String idempotencyKey) {

        // 1. Idempotency check
        transactionRepository.findByIdempotencyKey(idempotencyKey)
                .ifPresent(tx -> {
                    throw new DuplicateRequestException("Transfer already processed");
                });


        // 2. Get sender (from JWT)
        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        User sender = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet fromWallet = walletRepository.findByUser(sender)
                .orElseThrow(() -> new RuntimeException("Sender wallet not found"));

        // 3. Get receiver wallet
        Wallet toWallet = walletRepository.findById(request.getToWalletId())
                .orElseThrow(() -> new RuntimeException("Receiver wallet not found"));

        // 4. Balance check
        if (fromWallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // 5. Update balances
        fromWallet.setBalance(fromWallet.getBalance().subtract(request.getAmount()));
        toWallet.setBalance(toWallet.getBalance().add(request.getAmount()));

        // 6. Save transaction
        Transaction tx = new Transaction();
        tx.setFromWallet(fromWallet.getId());
        tx.setToWallet(toWallet.getId());
        tx.setAmount(request.getAmount());
        tx.setType("TRANSFER");
        tx.setStatus("SUCCESS");
        tx.setIdempotencyKey(idempotencyKey);

        transactionRepository.save(tx);

        // 7. Save wallets (optimistic lock applied here)
        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);
    }



}
