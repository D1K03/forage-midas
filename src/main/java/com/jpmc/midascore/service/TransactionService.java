package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TransactionService {
    private static final Logger LOG = LoggerFactory.getLogger(TransactionService.class);

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(UserRepository userRecordRepository, TransactionRepository transactionRecordRepository) {
        this.userRepository = userRecordRepository;
        this.transactionRepository = transactionRecordRepository;
    }

    @Transactional
    public void processTransaction(Transaction transactionDto) {
        Optional<UserRecord> senderOpt = userRepository.findById(transactionDto.getUserId());
        Optional<UserRecord> recipientOpt = userRepository.findById(transactionDto.getBalanceId());

        // 1. Validate sender and recipient exist
        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) {
            LOG.warn("Invalid transaction: Sender or recipient not found. Discarding.");
            return;
        }

        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();
        float amount = transactionDto.getAmount();

        // 2. Validate sender has sufficient balance
        if (sender.getBalance() < amount) {
            LOG.warn("Invalid transaction: Insufficient balance for sender {}. Discarding.", sender.getName());
            return;
        }

        // 3. Update balances
        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount);

        // 4. Record the valid transaction
        TransactionRecord record = new TransactionRecord(sender, recipient, amount);
        transactionRepository.save(record);

        LOG.info("Successfully processed transaction of {} from {} to {}", amount, sender.getName(), recipient.getName());
    }
}