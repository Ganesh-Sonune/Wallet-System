package com.example.Wallet.service;

import com.example.Wallet.dto.LoginRequest;
import com.example.Wallet.dto.LoginResponse;
import com.example.Wallet.dto.RegisterRequest;
import com.example.Wallet.entity.User;
import com.example.Wallet.entity.Wallet;
import com.example.Wallet.repository.UserRepository;
import com.example.Wallet.repository.WalletRepository;
import com.example.Wallet.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       WalletRepository walletRepository,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.jwtUtil = jwtUtil;
    }


    @Transactional
    public void register(RegisterRequest request) {

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");

        userRepository.save(user);

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        walletRepository.save(wallet);
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return new LoginResponse(token);
    }

}
