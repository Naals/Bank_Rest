package com.aslan.project.bank_rest.repository;

import com.aslan.project.bank_rest.entity.RefreshToken;
import com.aslan.project.bank_rest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
