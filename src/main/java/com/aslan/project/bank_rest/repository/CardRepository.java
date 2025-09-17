package com.aslan.project.bank_rest.repository;

import com.aslan.project.bank_rest.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    Page<Card> findByOwnerId(Long ownerId, Pageable pageable);

    Page<Card> findByOwnerIdAndCardNumberContainingIgnoreCase(Long ownerId, String query, Pageable pageable);

    Optional<Card> findByCardNumberAndOwnerId(String cardNumber, Long ownerId);

    Optional<Card> findByCardNumber(String cardNumber);
}
