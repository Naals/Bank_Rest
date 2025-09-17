package com.aslan.project.bank_rest.service;

import com.aslan.project.bank_rest.dto.*;
import com.aslan.project.bank_rest.dto.request.CardCreateRequest;
import com.aslan.project.bank_rest.dto.request.CardGetRequest;
import com.aslan.project.bank_rest.dto.request.TopUpRequest;
import com.aslan.project.bank_rest.dto.response.BalanceResponse;
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
    private final EncryptionUtil encryptionUtil;

    public CardService(CardRepository cr, UserRepository ur, EncryptionUtil encryptionUtil) {
        this.cardRepo = cr;
        this.userRepo = ur;
        this.encryptionUtil = encryptionUtil;
    }

    public CardDto create(CardCreateRequest req) {
        User user = userRepo.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Card c = new Card();
        c.setCardNumber(encryptionUtil.encrypt(req.getCardNumber()));
        c.setOwnerName(user.getUsername());
        c.setOwnerId(user.getId());
        c.setExpiryDate(req.getExpiry());
        c.setStatus(CardStatus.ACTIVE);

        cardRepo.save(c);

        String masked = EncryptionUtil.mask(req.getCardNumber());
        return CardDto.fromEntity(c, masked);
    }

    public Page<CardDto> listForUser(Long id, int page, int size) {
        return cardRepo.findByOwnerId(id, PageRequest.of(page, size))
                .map(c -> {
                    String masked = EncryptionUtil.mask(encryptionUtil.decrypt(c.getCardNumber()));
                    return CardDto.fromEntity(c, masked);
                });
    }

    public Card cardFind(String cardNumber, Long userId) {
        return cardRepo.findByCardNumberAndOwnerId(encryptionUtil.encrypt(cardNumber), userId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
    }

    public BalanceResponse getBalanceForUserCard(Long userId, CardGetRequest req) {
        var card = cardFind(req.getCardNumber(), userId);
        String masked = EncryptionUtil.mask(encryptionUtil.decrypt(card.getCardNumber()));
        return new BalanceResponse(masked, card.getBalance());
    }

    public Page<CardDto> searchUserCards(Long userId, String query, int page, int size) {
        return cardRepo.findByOwnerIdAndCardNumberContainingIgnoreCase(userId, query,
                        PageRequest.of(page, size))
                .map(c -> {
                    String masked = EncryptionUtil.mask(encryptionUtil.decrypt(c.getCardNumber()));
                    return CardDto.fromEntity(c, masked);
                });
    }

    @Transactional
    public void topUp(Long userId, TopUpRequest req) {
        var card = cardFind(req.getCardNumber(), userId);

        long amountInCents = req.getAmount().longValueExact();

        card.setBalance(card.getBalance() + amountInCents);
        cardRepo.save(card);
    }

    @Transactional
    public void deleteCard(CardGetRequest req) {
        Card card = cardRepo.findByCardNumber(encryptionUtil.encrypt(req.getCardNumber()))
                .orElseThrow(() -> new RuntimeException("Card not found"));
        cardRepo.deleteById(card.getId());
    }

    public Page<CardDto> listAll(int page, int size) {
        return cardRepo.findAll(PageRequest.of(page, size))
                .map(c -> {
                    String masked = EncryptionUtil.mask(encryptionUtil.decrypt(c.getCardNumber()));
                    return CardDto.fromEntity(c, masked);
                });
    }
}

