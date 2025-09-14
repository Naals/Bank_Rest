package com.aslan.project.bank_rest.service;

import com.aslan.project.bank_rest.dto.request.CardGetRequest;
import com.aslan.project.bank_rest.entity.BlockRequestEntity;
import com.aslan.project.bank_rest.entity.Card;
import com.aslan.project.bank_rest.entity.CardStatus;
import com.aslan.project.bank_rest.repository.BlockRequestRepository;
import com.aslan.project.bank_rest.repository.CardRepository;
import com.aslan.project.bank_rest.util.EncryptionUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class BlockRequestService {
    private final BlockRequestRepository repo;
    private final CardRepository cardRepo;

    public BlockRequestService(BlockRequestRepository r, CardRepository c) {
        this.repo = r;
        this.cardRepo = c;
    }

    @Transactional
    public void requestBlock(Long userId, CardGetRequest req) {
        var card = cardRepo.findByCardNumberAndOwnerId(EncryptionUtil.mask(req.getCardNumber()), userId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        BlockRequestEntity br = new BlockRequestEntity();
        br.setCard(card);
        br.setUserId(userId);
        br.setStatus("PENDING");
        br.setCreatedAt(Instant.now());

        repo.save(br);
    }


    @Transactional
    public void approveRequest(Long requestId) {
        var br = repo.findById(requestId).orElseThrow();
        br.setStatus("APPROVED");

        repo.save(br);

        Card c = br.getCard();
        c.setStatus(CardStatus.BLOCKED);
        cardRepo.save(c);
    }

    @Transactional
    public void rejectRequest(Long requestId) {
        var br = repo.findById(requestId).orElseThrow();
        br.setStatus("REJECTED");
        repo.save(br);
    }
}
