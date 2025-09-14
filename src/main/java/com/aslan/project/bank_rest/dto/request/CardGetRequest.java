package com.aslan.project.bank_rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CardGetRequest {
    @NotBlank private String cardNumber;
}
