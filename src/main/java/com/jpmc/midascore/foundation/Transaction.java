package com.jpmc.midascore.foundation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Use this annotation to ignore any fields in the JSON that don't match fields in the class
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction {
    // These are the correct field names for the project
    private long userId;
    private long balanceId;
    private float amount;

    // The required no-argument constructor for Jackson
    public Transaction() {
    }

    // The constructor used by the producer
    public Transaction(long userId, long balanceId, float amount) {
        this.userId = userId;
        this.balanceId = balanceId;
        this.amount = amount;
    }

    // Standard getters and setters
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getBalanceId() {
        return balanceId;
    }

    public void setBalanceId(long balanceId) {
        this.balanceId = balanceId;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Transaction{userId=" + userId + ", balanceId=" + balanceId + ", amount=" + amount + "}";
    }
}