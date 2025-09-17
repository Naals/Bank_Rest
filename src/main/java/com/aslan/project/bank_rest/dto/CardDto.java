package com.aslan.project.bank_rest.dto;

import com.aslan.project.bank_rest.entity.Card;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CardDto {
    private Long id;
    private String maskedNumber;
    private String ownerName;
    private LocalDate expiry;
    private String status;
    private BigDecimal balance;

    public static CardDto fromEntity(Card c, String maskedNumber) {
        CardDto d = new CardDto();
        d.setId(c.getId());
        d.setMaskedNumber(maskedNumber);
        d.setOwnerName(c.getOwnerName());
        d.setExpiry(c.getExpiryDate());
        d.setStatus(c.getStatus().name());
        d.setBalance(BigDecimal.valueOf(c.getBalance()));
        return d;
    }
}
