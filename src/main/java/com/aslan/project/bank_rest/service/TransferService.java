package com.aslan.project.bank_rest.service;

import com.aslan.project.bank_rest.entity.*;
import com.aslan.project.bank_rest.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransferService {
    private final CardRepository cardRepo;
    private final TransactionRepository txRepo;
    public TransferService(CardRepository cr, TransactionRepository tr) { this.cardRepo = cr; this.txRepo = tr; }

    @Transactional
    public void transfer(Long fromId, Long toId, BigDecimal amount, Long userId) {
        if (fromId.equals(toId)) throw new RuntimeException("Cannot transfer to same card");
        Card from = cardRepo.findById(fromId).orElseThrow(() -> new RuntimeException("From card not found"));
        Card to = cardRepo.findById(toId).orElseThrow(() -> new RuntimeException("To card not found"));

        if (!from.getOwnerId().equals(userId) || !to.getOwnerId().equals(userId)) throw new RuntimeException("Cards must belong to same user");
        if (from.getStatus() != CardStatus.ACTIVE || to.getStatus() != CardStatus.ACTIVE) throw new RuntimeException("Card not active");

        long amountCents = amount.movePointRight(2).longValueExact();
        if (from.getBalance() < amountCents) throw new RuntimeException("Insufficient funds");

        from.setBalance(from.getBalance() - amountCents);
        to.setBalance(to.getBalance() + amountCents);

        cardRepo.save(from);
        cardRepo.save(to);

        TransactionEntity tx = new TransactionEntity();
        tx.setFromCard(from);
        tx.setToCard(to);
        tx.setAmountCents(amountCents);
        tx.setCurrency("KZT");
        tx.setStatus("COMPLETED");
        txRepo.save(tx);
    }
}

