package com.aslan.project.bank_rest.controller;

import com.aslan.project.bank_rest.dto.*;
import com.aslan.project.bank_rest.dto.request.CardCreateRequest;
import com.aslan.project.bank_rest.dto.request.CardGetRequest;
import com.aslan.project.bank_rest.dto.request.TopUpRequest;
import com.aslan.project.bank_rest.dto.response.ApiResponse;
import com.aslan.project.bank_rest.dto.response.BalanceResponse;
import com.aslan.project.bank_rest.repository.UserRepository;
import com.aslan.project.bank_rest.service.BlockRequestService;
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
    private final BlockRequestService blockRequestService;

    public CardController(CardService cs, UserRepository ur, BlockRequestService bs) {
        this.cardService = cs;
        this.userRepo = ur;
        this.blockRequestService = bs;
    }

    @PostMapping("/create")
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

    @GetMapping("/balance")
    public ResponseEntity<BalanceResponse> balance(@RequestBody CardGetRequest req,
                                                   @AuthenticationPrincipal UserDetails ud) {
        var user = userRepo.findByUsername(ud.getUsername()).orElseThrow();
        return ResponseEntity.ok(cardService.getBalanceForUserCard(user.getId(), req));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CardDto>> search(@AuthenticationPrincipal UserDetails ud,
                                                @RequestParam String query,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        var user = userRepo.findByUsername(ud.getUsername()).orElseThrow();
        return ResponseEntity.ok(cardService.searchUserCards(user.getId(), query, page, size));
    }

    @PostMapping("/topup")
    public ResponseEntity<?> topUp(@AuthenticationPrincipal UserDetails ud,
                                   @Valid @RequestBody TopUpRequest req) {
        var user = userRepo.findByUsername(ud.getUsername()).orElseThrow();
        cardService.topUp(user.getId(), req);
        return ResponseEntity.ok(new ApiResponse("Balance topped up successfully"));
    }

    @PostMapping("/request-block")
    public ResponseEntity<ApiResponse> requestBlock(@AuthenticationPrincipal UserDetails ud,
                                                    @RequestBody CardGetRequest req) {
        var user = userRepo.findByUsername(ud.getUsername()).orElseThrow();
        blockRequestService.requestBlock(user.getId(), req);
        return ResponseEntity.ok(new ApiResponse("Block request submitted"));
    }

    @PutMapping("/block-requests/{id}/approve")
    public ResponseEntity<ApiResponse> approve(@PathVariable Long id) {
        blockRequestService.approveRequest(id);
        return ResponseEntity.ok(new ApiResponse("Card blocked successfully"));
    }

    @PutMapping("/block-requests/{id}/reject")
    public ResponseEntity<ApiResponse> reject(@PathVariable Long id) {
        blockRequestService.rejectRequest(id);
        return ResponseEntity.ok(new ApiResponse("Block request rejected"));
    }



}

