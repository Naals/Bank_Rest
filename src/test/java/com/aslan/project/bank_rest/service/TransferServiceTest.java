package com.aslan.project.bank_rest.service;

import com.aslan.project.bank_rest.entity.Card;
import com.aslan.project.bank_rest.entity.CardStatus;
import com.aslan.project.bank_rest.entity.TransactionEntity;
import com.aslan.project.bank_rest.repository.CardRepository;
import com.aslan.project.bank_rest.repository.TransactionRepository;
import com.aslan.project.bank_rest.util.EncryptionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransferServiceTest {

    @Mock private CardRepository cardRepo;
    @Mock private TransactionRepository txRepo;
    @Mock private EncryptionUtil encryptionUtil;
    @InjectMocks private TransferService transferService;

    private Card from;
    private Card to;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        from = new Card();
        from.setId(1L);
        from.setOwnerId(100L);
        from.setCardNumber("ENC(1111)");
        from.setStatus(CardStatus.ACTIVE);
        from.setBalance(1000L);

        to = new Card();
        to.setId(2L);
        to.setOwnerId(100L);
        to.setCardNumber("ENC(2222)");
        to.setStatus(CardStatus.ACTIVE);
        to.setBalance(500L);

        when(encryptionUtil.encrypt("1111")).thenReturn("ENC(1111)");
        when(encryptionUtil.encrypt("2222")).thenReturn("ENC(2222)");
    }


    @Test
    void transfer_shouldMoveMoneySuccessfully() {
        when(cardRepo.findByCardNumber(anyString())).thenAnswer(inv -> {
            String masked = inv.getArgument(0, String.class);
            if (masked.equals(from.getCardNumber())) return Optional.of(from);
            if (masked.equals(to.getCardNumber()))return Optional.of(to);
            return Optional.empty();
        });

        transferService.transfer("1111", "2222", BigDecimal.valueOf(300), 100L);

        assertEquals(700L, from.getBalance());
        assertEquals(800L, to.getBalance());
        verify(cardRepo, times(2)).save(any(Card.class));
        verify(txRepo).save(any(TransactionEntity.class));
    }

    @Test
    void transfer_shouldFailForSameCard() {
        assertThrows(RuntimeException.class, () ->
                transferService.transfer("1111", "1111", BigDecimal.valueOf(100), 100L));
    }

    @Test
    void transfer_shouldFailIfFromCardNotFound() {
        when(cardRepo.findByCardNumber(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                transferService.transfer("9999", "2222", BigDecimal.valueOf(100), 100L));
    }

    @Test
    void transfer_shouldFailIfCardsBelongToDifferentUsers() {
        to.setOwnerId(200L);

        when(cardRepo.findByCardNumber(from.getCardNumber())).thenReturn(Optional.of(from));
        when(cardRepo.findByCardNumber(to.getCardNumber())).thenReturn(Optional.of(to));

        assertThrows(RuntimeException.class, () ->
                transferService.transfer("1111", "2222", BigDecimal.valueOf(100), 100L));
    }

    @Test
    void transfer_shouldFailIfCardNotActive() {
        from.setStatus(CardStatus.BLOCKED);

        when(cardRepo.findByCardNumber(from.getCardNumber())).thenReturn(Optional.of(from));
        when(cardRepo.findByCardNumber(to.getCardNumber())).thenReturn(Optional.of(to));

        assertThrows(RuntimeException.class, () ->
                transferService.transfer("1111", "2222", BigDecimal.valueOf(100), 100L));
    }

    @Test
    void transfer_shouldFailIfInsufficientFunds() {
        from.setBalance(50L);

        when(cardRepo.findByCardNumber(from.getCardNumber())).thenReturn(Optional.of(from));
        when(cardRepo.findByCardNumber(to.getCardNumber())).thenReturn(Optional.of(to));

        assertThrows(RuntimeException.class, () ->
                transferService.transfer("1111", "2222", BigDecimal.valueOf(300), 100L));
    }
}
