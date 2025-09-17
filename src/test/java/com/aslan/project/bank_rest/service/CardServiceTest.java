package com.aslan.project.bank_rest.service;

import com.aslan.project.bank_rest.dto.request.CardCreateRequest;
import com.aslan.project.bank_rest.dto.request.CardGetRequest;
import com.aslan.project.bank_rest.dto.request.TopUpRequest;
import com.aslan.project.bank_rest.dto.response.BalanceResponse;
import com.aslan.project.bank_rest.entity.Card;
import com.aslan.project.bank_rest.entity.CardStatus;
import com.aslan.project.bank_rest.entity.User;
import com.aslan.project.bank_rest.repository.CardRepository;
import com.aslan.project.bank_rest.repository.UserRepository;
import com.aslan.project.bank_rest.util.EncryptionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceTest {

    @Mock private CardRepository cardRepo;
    @Mock private UserRepository userRepo;
    @Mock private EncryptionUtil encryptionUtil;

    @InjectMocks private CardService cardService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);

        // дефолтное поведение для мока
        when(encryptionUtil.encrypt(anyString())).thenAnswer(inv -> "ENC(" + inv.getArgument(0) + ")");
        when(encryptionUtil.decrypt(anyString())).thenAnswer(inv -> {
            String val = inv.getArgument(0);
            return val.replace("ENC(", "").replace(")", "");
        });
    }

    @Test
    void create_shouldReturnCardDto() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");

        CardCreateRequest req = new CardCreateRequest();
        req.setUserId(1L);
        req.setCardNumber("1234567890123456");
        req.setExpiry(LocalDate.of(2030,12,31));

        Card card = new Card();
        card.setId(10L);
        card.setOwnerId(1L);
        card.setCardNumber("ENC(1234567890123456)");
        card.setStatus(CardStatus.ACTIVE);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(cardRepo.save(any(Card.class))).thenReturn(card);

        var dto = cardService.create(req);

        assertEquals("test", dto.getOwnerName());
        assertEquals("ACTIVE", dto.getStatus());
        verify(cardRepo).save(any(Card.class));
    }

    @Test
    void topUp_shouldIncreaseBalance() {
        Card card = new Card();
        card.setId(1L);
        card.setOwnerId(1L);
        card.setCardNumber("ENC(1234567890123456)");
        card.setBalance(1000L);

        TopUpRequest req = new TopUpRequest();
        req.setCardNumber("1234567890123456");
        req.setAmount(BigDecimal.valueOf(500));

        when(cardRepo.findByCardNumberAndOwnerId("ENC(1234567890123456)", 1L))
                .thenReturn(Optional.of(card));

        cardService.topUp(1L, req);

        assertEquals(1500L, card.getBalance());
        verify(cardRepo).save(card);
    }

    @Test
    void getBalanceForUserCard_shouldReturnBalance() {
        Card card = new Card();
        card.setOwnerId(1L);
        card.setCardNumber("ENC(1234567890123456)");
        card.setBalance(2000L);

        CardGetRequest req = new CardGetRequest();
        req.setCardNumber("1234567890123456");

        when(cardRepo.findByCardNumberAndOwnerId("ENC(1234567890123456)", 1L))
                .thenReturn(Optional.of(card));

        BalanceResponse resp = cardService.getBalanceForUserCard(1L, req);

        assertEquals(2000L, resp.getBalance());
        assertTrue(resp.getCardNumber().endsWith("3456")); // вот тут уже маска
    }
}
