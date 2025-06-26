package com.jpmc.midascore.controller;

import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.service.TransactionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BalanceController {

    private final TransactionService transactionService;

    public BalanceController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/balance")
    public Balance getBalance(@RequestParam("userId") Long userId) {
        return transactionService.getBalanceForUser(userId);
    }
}