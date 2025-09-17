package com.aslan.project.bank_rest.dto.response;

public record BalanceResponse(String cardNumber, Long balance) {
    public String getCardNumber() {
        return cardNumber;
    }

    public Long getBalance() {
        return balance;
    }
}
