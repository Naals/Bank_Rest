package com.aslan.project.bank_rest.service;

import com.aslan.project.bank_rest.dto.*;
import com.aslan.project.bank_rest.dto.request.CardCreateRequest;
import com.aslan.project.bank_rest.dto.request.CardGetRequest;
import com.aslan.project.bank_rest.dto.request.TopUpRequest;
import com.aslan.project.bank_rest.dto.response.BalanceResponse;
import com.aslan.project.bank_rest.dto.response.BlockResponse;
import com.aslan.project.bank_rest.entity.*;
import com.aslan.project.bank_rest.repository.*;
import com.aslan.project.bank_rest.util.EncryptionUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Card cardFind (String cardNumber, Long userId) {
        return cardRepo.findByCardNumberAndOwnerId(EncryptionUtil.mask(cardNumber), userId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
    }

    @Transactional
    public BlockResponse blockCard(Long userId, CardGetRequest req) {
        var card = cardFind(req.getCardNumber(), userId);
        card.setStatus(CardStatus.BLOCKED);
        cardRepo.save(card);
        return new BlockResponse(card.getCardNumber(), CardStatus.BLOCKED);
    }

    public BalanceResponse getBalanceForUserCard(Long userId, CardGetRequest req) {
        var card = cardFind(req.getCardNumber(), userId);
        return new BalanceResponse(card.getCardNumber(), card.getBalance());
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
