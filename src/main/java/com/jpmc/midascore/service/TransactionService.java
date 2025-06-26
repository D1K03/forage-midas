package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository; // Corrected import
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import com.jpmc.midascore.foundation.Balance;
import java.util.Optional;

@Service
public class TransactionService {
    private static final Logger LOG = LoggerFactory.getLogger(TransactionService.class);
    private static final String INCENTIVE_API_URL = "http://localhost:8080/incentive";

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;

    public TransactionService(UserRepository userRecordRepository, TransactionRepository transactionRecordRepository, RestTemplate restTemplate) {
        this.userRepository = userRecordRepository;
        this.transactionRepository = transactionRecordRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public void processTransaction(Transaction transactionDto) {
        Optional<UserRecord> senderOpt = userRepository.findById(transactionDto.getUserId());
        Optional<UserRecord> recipientOpt = userRepository.findById(transactionDto.getBalanceId());

        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) {
            LOG.warn("Invalid transaction: Sender or recipient not found. Discarding.");
            return;
        }

        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();
        float amount = transactionDto.getAmount();

        if (sender.getBalance() < amount) {
            LOG.warn("Invalid transaction: Insufficient balance for sender {}. Discarding.", sender.getName());
            return;
        }

        Incentive incentiveResponse = restTemplate.postForObject(INCENTIVE_API_URL, transactionDto, Incentive.class);
        float incentiveAmount = (incentiveResponse != null) ? incentiveResponse.getAmount() : 0.0f;

        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount);


        TransactionRecord record = new TransactionRecord(sender, recipient, amount, incentiveAmount);
        transactionRepository.save(record);

        LOG.info("Successfully processed transaction of {} (incentive: {}) from {} to {}", amount, incentiveAmount, sender.getName(), recipient.getName());
    }

    public Balance getBalanceForUser(Long userId) {
        Optional<UserRecord> userOpt = userRepository.findById(userId);

        if (userOpt.isPresent()) {
            return new Balance(userOpt.get().getBalance());
        } else {
            LOG.warn("User with ID {} not found. Returning zero balance.", userId);
            return new Balance(0.0f);
        }
    }
}