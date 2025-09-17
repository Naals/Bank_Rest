package com.aslan.project.bank_rest.service;

import com.aslan.project.bank_rest.entity.*;
import com.aslan.project.bank_rest.repository.*;
import com.aslan.project.bank_rest.util.EncryptionUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class TransferService {
    private final CardRepository cardRepo;
    private final TransactionRepository txRepo;
    private final EncryptionUtil encryptionUtil;
    public TransferService(CardRepository cr, TransactionRepository tr, EncryptionUtil encryptionUtil) { this.cardRepo = cr; this.txRepo = tr; this.encryptionUtil = encryptionUtil; }

    @Transactional
    public void transfer(String fromCard, String toCard, BigDecimal amount, Long userId) {
        if (fromCard.equals(toCard)) throw new RuntimeException("Cannot transfer to same card");
        Card from = cardRepo.findByCardNumber(encryptionUtil.encrypt(fromCard)).orElseThrow(() -> new RuntimeException("From card not found"));
        Card to = cardRepo.findByCardNumber(encryptionUtil.encrypt(toCard)).orElseThrow(() -> new RuntimeException("To card not found"));

        if (!from.getOwnerId().equals(userId) || !to.getOwnerId().equals(userId)) throw new RuntimeException("Cards must belong to same user");
        if (from.getStatus() != CardStatus.ACTIVE || to.getStatus() != CardStatus.ACTIVE) throw new RuntimeException("Card not active");

        long amountCents = amount.longValueExact();
        if (from.getBalance() < amountCents) throw new RuntimeException("Insufficient funds");

        from.setBalance(from.getBalance() - amountCents);
        to.setBalance(to.getBalance() + amountCents);

        cardRepo.save(from);
        cardRepo.save(to);

        TransactionEntity tx = new TransactionEntity();
        tx.setFromCard(from);
        tx.setToCard(to);
        tx.setAmount(amountCents);
        tx.setCreatedAt(Instant.now());
        txRepo.save(tx);
    }
}

