package com.example.Wallet.controller;

import com.example.Wallet.entity.Transaction;
import com.example.Wallet.entity.User;
import com.example.Wallet.entity.Wallet;
import com.example.Wallet.service.AdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllWallets_success() throws Exception {

        User user = new User();
        user.setEmail("admin@test.com");

        Wallet wallet1 = new Wallet();
        wallet1.setUser(user);
        wallet1.setBalance(BigDecimal.valueOf(1000));

        Wallet wallet2 = new Wallet();
        wallet2.setUser(user);
        wallet2.setBalance(BigDecimal.valueOf(2000));

        when(adminService.getAllWallets())
                .thenReturn(List.of(wallet1, wallet2));

        mockMvc.perform(get("/admin/wallets"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllTransactions_success() throws Exception {

        Transaction tx = new Transaction();
        tx.setType("TRANSFER");
        tx.setStatus("SUCCESS");
        tx.setAmount(BigDecimal.valueOf(500));
        tx.setFromWallet(1L);
        tx.setToWallet(2L);

        when(adminService.getAllTransactions())
                .thenReturn(List.of(tx));

        mockMvc.perform(get("/admin/transactions"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void adminApi_forbidden_forUser() throws Exception {

        mockMvc.perform(get("/admin/wallets"))
                .andExpect(status().isForbidden());
    }
}
