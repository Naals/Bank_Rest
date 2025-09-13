package com.aslan.project.bank_rest.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
public class CardDto {
    private Long id;
    private String maskedNumber;
    private String ownerName;
    private LocalDate expiry;
    private String status;
    private BigDecimal balance; // in major units
}
