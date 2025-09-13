package com.aslan.project.bank_rest.service;

import com.aslan.project.bank_rest.dto.*;
import com.aslan.project.bank_rest.entity.*;
import com.aslan.project.bank_rest.repository.*;
import com.aslan.project.bank_rest.util.EncryptionUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CardService {
    private final CardRepository cardRepo;
    private final UserRepository userRepo;

    public CardService(CardRepository cr, UserRepository ur) { this.cardRepo = cr; this.userRepo = ur;}

    public CardDto create(CardCreateRequest req) {
        User user = userRepo.findById(req.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
        Card c = new Card();
        c.setCardNumber(EncryptionUtil.mask(req.getCardNumber()));
        c.setOwnerName(req.getOwnerName());
        c.setOwnerId(user.getId());
        c.setExpiryDate(req.getExpiry());
        c.setStatus(CardStatus.ACTIVE);

        c = cardRepo.save(c);
        return toDto(c);
    }

    public Page<CardDto> listForUser(Long id, int page, int size) {
        var p = cardRepo.findByOwnerId(id, PageRequest.of(page, size));
        return p.map(this::toDto);
    }

    public Optional<Card> findById(Long id) { return cardRepo.findById(id); }

    @Transactional
    public void blockCard(Long id) {
        Card c = cardRepo.findById(id).orElseThrow();
        c.setStatus(CardStatus.BLOCKED);
        cardRepo.save(c);
    }

    public CardDto toDto(Card c) {
        CardDto d = new CardDto();
        d.setId(c.getId());
        d.setMaskedNumber(c.getCardNumber());
        d.setOwnerName(c.getOwnerName());
        d.setExpiry(c.getExpiryDate());
        d.setStatus(c.getStatus().name());
        d.setBalance(BigDecimal.valueOf(c.getBalance(), 2));
        return d;
    }
}
