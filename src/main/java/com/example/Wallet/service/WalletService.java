package com.example.Wallet.service;

import com.example.Wallet.dto.*;
import com.example.Wallet.entity.Transaction;
import com.example.Wallet.entity.User;
import com.example.Wallet.entity.Wallet;
import com.example.Wallet.exception.DuplicateRequestException;
import com.example.Wallet.exception.InsufficientBalanceException;
import com.example.Wallet.exception.ResourceNotFoundException;
import com.example.Wallet.repository.TransactionRepository;
import com.example.Wallet.repository.UserRepository;
import com.example.Wallet.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    // =========================
    // GET MY WALLET
    // =========================
    public WalletResponse getMyWallet() {

        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Wallet not found"));

        return new WalletResponse(wallet.getId(), wallet.getBalance());
    }

    // =========================
    // ADD MONEY
    // =========================
    @Transactional
    public WalletResponse addMoney(AddMoneyRequest request, String idempotencyKey) {

        // 1️⃣ Idempotency check
        transactionRepository.findByIdempotencyKey(idempotencyKey)
                .ifPresent(tx -> {
                    throw new DuplicateRequestException("Duplicate request");
                });

        // 2️⃣ Get authenticated user
        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Wallet not found"));

        // 3️⃣ Update balance
        wallet.setBalance(wallet.getBalance().add(request.getAmount()));

        // 4️⃣ Save transaction
        Transaction tx = new Transaction();
        tx.setToWallet(wallet.getId());
        tx.setAmount(request.getAmount());
        tx.setType("ADD");
        tx.setStatus("SUCCESS");
        tx.setIdempotencyKey(idempotencyKey);

        transactionRepository.save(tx);

        // 5️⃣ Save wallet
        Wallet updatedWallet = walletRepository.save(wallet);

        return new WalletResponse(
                updatedWallet.getId(),
                updatedWallet.getBalance()
        );
    }

    // =========================
    // TRANSFER MONEY
    // =========================
    @Transactional
    public TransferResponse transferMoney(TransferRequest request, String idempotencyKey) {

        // 1️⃣ Idempotency check
        transactionRepository.findByIdempotencyKey(idempotencyKey)
                .ifPresent(tx -> {
                    throw new DuplicateRequestException("Transfer already processed");
                });

        // 2️⃣ Get sender
        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        User sender = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Wallet fromWallet = walletRepository.findByUser(sender)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Sender wallet not found"));

        // 3️⃣ Get receiver wallet
        Wallet toWallet = walletRepository.findById(request.getToWalletId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Receiver wallet not found"));

        // 4️⃣ Balance check (🔥 FIXED)
        if (fromWallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        // 5️⃣ Update balances
        fromWallet.setBalance(
                fromWallet.getBalance().subtract(request.getAmount())
        );
        toWallet.setBalance(
                toWallet.getBalance().add(request.getAmount())
        );

        // 6️⃣ Save transaction
        Transaction tx = new Transaction();
        tx.setFromWallet(fromWallet.getId());
        tx.setToWallet(toWallet.getId());
        tx.setAmount(request.getAmount());
        tx.setType("TRANSFER");
        tx.setStatus("SUCCESS");
        tx.setIdempotencyKey(idempotencyKey);

        transactionRepository.save(tx);

        // 7️⃣ Save wallets
        Wallet updatedFrom = walletRepository.save(fromWallet);
        Wallet updatedTo = walletRepository.save(toWallet);

        return new TransferResponse(
                "Transfer successful",
                updatedFrom.getBalance(),
                updatedTo.getBalance()
        );
    }

    // =========================
    // GET MY TRANSACTIONS
    // =========================
    public Page<TransactionResponse> getMyTransactions(int page, int size) {

        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Wallet not found"));

        return transactionRepository
                .findByFromWalletOrToWallet(
                        wallet.getId(),
                        wallet.getId(),
                        PageRequest.of(page, size, Sort.by("createdAt").descending())
                )
                .map(tx -> new TransactionResponse(
                        tx.getId(),
                        tx.getType(),
                        tx.getStatus(),
                        tx.getAmount(),
                        tx.getFromWallet(),
                        tx.getToWallet(),
                        tx.getCreatedAt()
                ));
    }
}
