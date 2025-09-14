package com.aslan.project.bank_rest.service;


import com.aslan.project.bank_rest.dto.request.*;
import com.aslan.project.bank_rest.dto.response.*;
import com.aslan.project.bank_rest.entity.*;
import com.aslan.project.bank_rest.repository.*;
import com.aslan.project.bank_rest.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final RefreshTokenRepository refreshRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final long refreshValidityDays;

    public AuthService(UserRepository ur, RefreshTokenRepository rr, PasswordEncoder pe, JwtUtil ju,
                       @org.springframework.beans.factory.annotation.Value("${jwt.refresh-token-validity-days}") long refreshValidityDays) {
        this.userRepo = ur; this.refreshRepo = rr; this.passwordEncoder = pe; this.jwtUtil = ju; this.refreshValidityDays = refreshValidityDays;
    }

    public void register(RegisterRequest req) {
        if (userRepo.findByUsername(req.getUsername()).isPresent()) throw new RuntimeException("Username exists");
        User u = new User();
        u.setUsername(req.getUsername());
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        u.setRoles(req.getRole().equalsIgnoreCase("admin") ? "ROLE_ADMIN" : "ROLE_USER");
        userRepo.save(u);
    }

    public AuthResponse login(LoginRequest req) {
        User u = userRepo.findByUsername(req.getUsername()).orElseThrow(() -> new RuntimeException("Bad credentials"));
        if (!passwordEncoder.matches(req.getPassword(), u.getPasswordHash())) throw new RuntimeException("Bad credentials");
        String access = jwtUtil.generateAccessToken(u.getUsername(), u.getRoles());
        RefreshToken refresh = new RefreshToken();
        refresh.setUser(u);
        refresh.setToken(java.util.UUID.randomUUID().toString());
        refresh.setExpiryDate(Instant.now().plusSeconds(refreshValidityDays * 24 * 3600));
        refreshRepo.save(refresh);
        return new AuthResponse(access, refresh.getToken());
    }

    public AuthResponse refresh(RefreshRequest req) {
        RefreshToken rt = refreshRepo.findByToken(req.getRefreshToken()).orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        if (rt.getExpiryDate().isBefore(Instant.now())) { refreshRepo.delete(rt); throw new RuntimeException("Refresh expired"); }
        String access = jwtUtil.generateAccessToken(rt.getUser().getUsername(), rt.getUser().getRoles());
        return new AuthResponse(access, rt.getToken());
    }

    public void logout(RefreshRequest req) {
        refreshRepo.findByToken(req.getRefreshToken()).ifPresent(refreshRepo::delete);
    }
}

