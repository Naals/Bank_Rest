package com.aslan.project.bank_rest.controller;

import com.aslan.project.bank_rest.dto.TransferRequest;
import com.aslan.project.bank_rest.repository.UserRepository;
import com.aslan.project.bank_rest.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {
    private final TransferService transferService;
    private final UserRepository userRepo;
    public TransferController(TransferService ts, UserRepository ur) { this.transferService = ts; this.userRepo = ur; }

    @PostMapping
    public ResponseEntity<?> transfer(@AuthenticationPrincipal UserDetails ud, @Valid @RequestBody TransferRequest req) {
        var user = userRepo.findByUsername(ud.getUsername()).orElseThrow();
        transferService.transfer(req.getFromCardId(), req.getToCardId(), req.getAmount(), user.getId());
        return ResponseEntity.ok(new com.aslan.project.bank_rest.dto.ApiResponse("Transfer completed"));
    }
}

