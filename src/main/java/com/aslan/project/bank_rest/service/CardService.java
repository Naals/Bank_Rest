package com.aslan.project.bank_rest.service;

import com.aslan.project.bank_rest.dto.*;
import com.aslan.project.bank_rest.dto.request.CardCreateRequest;
import com.aslan.project.bank_rest.dto.request.TopUpRequest;
import com.aslan.project.bank_rest.dto.response.BalanceResponse;
import com.aslan.project.bank_rest.entity.*;
import com.aslan.project.bank_rest.repository.*;
import com.aslan.project.bank_rest.util.EncryptionUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        c.setOwnerName(user.getUsername());
        c.setOwnerId(user.getId());
        c.setExpiryDate(req.getExpiry());
        c.setStatus(CardStatus.ACTIVE);

        c = cardRepo.save(c);
        return CardDto.fromEntity(c);
    }

    public Page<CardDto> listForUser(Long id, int page, int size) {
        return cardRepo.findByOwnerId(id, PageRequest.of(page, size))
                .map(CardDto::fromEntity);
    }

    public Optional<Card> findById(Long id) { return cardRepo.findById(id); }

    @Transactional
    public void blockCard(Long id) {
        Card c = cardRepo.findById(id).orElseThrow();
        c.setStatus(CardStatus.BLOCKED);
        cardRepo.save(c);
    }

    public BalanceResponse getBalanceForUserCard(Long userId, Long cardId) {
        var card = cardRepo.findByIdAndOwnerId(cardId, userId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        return new BalanceResponse(card.getId(), card.getBalance());
    }

    public Page<CardDto> searchUserCards(Long userId, String query, int page, int size) {
        return cardRepo.findByOwnerIdAndCardNumberContainingIgnoreCase(userId, query,
                PageRequest.of(page, size)).map(CardDto::fromEntity);
    }

    @Transactional
    public void topUp(Long userId, TopUpRequest req) {
        var card = cardRepo.findByCardNumberAndOwnerId(
                EncryptionUtil.mask(req.getCardNumber()),
                userId
        ).orElseThrow(() -> new RuntimeException("Card not found"));

        long amountInCents = req.getAmount().longValueExact();

        card.setBalance(card.getBalance() + amountInCents);
        cardRepo.save(card);
    }


}
