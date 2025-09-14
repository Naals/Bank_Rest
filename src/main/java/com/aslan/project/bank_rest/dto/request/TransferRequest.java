package com.aslan.project.bank_rest.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class TransferRequest {
    @NotNull private String fromCardNumber;
    @NotNull private String toCardNumber;
    @NotNull @DecimalMin("0.01") private BigDecimal amount;
}
