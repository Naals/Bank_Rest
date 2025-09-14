package com.aslan.project.bank_rest.dto.response;

import com.aslan.project.bank_rest.entity.CardStatus;

public record BlockResponse(String cardNumber, CardStatus cardStatus) { }
