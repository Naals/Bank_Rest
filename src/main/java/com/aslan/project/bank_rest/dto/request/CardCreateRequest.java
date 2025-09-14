package com.aslan.project.bank_rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CardCreateRequest {
    @NotBlank private String cardNumber;
    @NotNull private java.time.LocalDate expiry;
    private Long userId;
}

