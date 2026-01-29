package com.example.Wallet.service;

import com.example.Wallet.dto.AddMoneyRequest;
import com.example.Wallet.dto.TransferRequest;
import com.example.Wallet.dto.WalletResponse;
import com.example.Wallet.entity.Transaction;
import com.example.Wallet.entity.User;
import com.example.Wallet.entity.Wallet;
import com.example.Wallet.repository.TransactionRepository;
import com.example.Wallet.repository.UserRepository;
import com.example.Wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class WalletServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private WalletService walletService;

    private User user;
    private Wallet wallet;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Fake authenticated user
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "user@test.com", null, null
                )
        );

        user = new User();
        user.setEmail("user@test.com");

        wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(BigDecimal.valueOf(1000));

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(user));

        when(walletRepository.findByUser(any(User.class)))
                .thenReturn(Optional.of(wallet));
    }

    // -------------------- getMyWallet --------------------

    @Test
    void getMyWallet_success() {

        WalletResponse response = walletService.getMyWallet();

        assertEquals(BigDecimal.valueOf(1000), response.getBalance());
    }

    // -------------------- addMoney --------------------

    @Test
    void addMoney_success() {

        AddMoneyRequest request = new AddMoneyRequest();
        request.setAmount(BigDecimal.valueOf(500));

        when(transactionRepository.findByIdempotencyKey(anyString()))
                .thenReturn(Optional.empty());

        when(walletRepository.save(any(Wallet.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WalletResponse response =
                walletService.addMoney(request, "idem-key-1");

        assertEquals(BigDecimal.valueOf(1500), response.getBalance());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    // -------------------- transferMoney --------------------

    @Test
    void transferMoney_success() {

        Wallet receiverWallet = new Wallet();
        receiverWallet.setBalance(BigDecimal.valueOf(500));

        TransferRequest request = new TransferRequest();
        request.setAmount(BigDecimal.valueOf(300));
        request.setToWalletId(1L);

        when(transactionRepository.findByIdempotencyKey(anyString()))
                .thenReturn(Optional.empty());

        when(walletRepository.findById(anyLong()))
                .thenReturn(Optional.of(receiverWallet));

        // 🔥 IMPORTANT FIX
        when(walletRepository.save(any(Wallet.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        walletService.transferMoney(request, "idem-key-2");

        assertEquals(BigDecimal.valueOf(700), wallet.getBalance());
        assertEquals(BigDecimal.valueOf(800), receiverWallet.getBalance());

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

}
