package com.aslan.project.bank_rest.controller;

import com.aslan.project.bank_rest.dto.*;
import com.aslan.project.bank_rest.repository.UserRepository;
import com.aslan.project.bank_rest.service.CardService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
public class CardController {
    private final CardService cardService;
    private final UserRepository userRepo;

    public CardController(CardService cs, UserRepository ur) { this.cardService = cs; this.userRepo = ur; }

    @PostMapping
    public ResponseEntity<CardDto> createCard(@Valid @RequestBody CardCreateRequest req) {
        return ResponseEntity.ok(cardService.create(req));
    }

    @GetMapping
    public ResponseEntity<Page<CardDto>> myCards(@AuthenticationPrincipal UserDetails ud,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        var user = userRepo.findByUsername(ud.getUsername()).orElseThrow();
        return ResponseEntity.ok(cardService.listForUser(user.getId(), page, size));
    }

    @PutMapping("/{id}/block")
    public ResponseEntity<?> block(@PathVariable Long id) {
        cardService.blockCard(id);
        return ResponseEntity.ok(new com.aslan.project.bank_rest.dto.ApiResponse("Blocked"));
    }
}
