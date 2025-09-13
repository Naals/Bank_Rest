package com.aslan.project.bank_rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CardCreateRequest {
    @NotBlank private String cardNumber; // plain number on creation
    @NotBlank private String ownerName;
    @NotNull private java.time.LocalDate expiry;
    private Long userId; // ADMIN provides
}

