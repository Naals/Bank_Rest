package com.aslan.project.bank_rest.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RefreshRequest {
    private String refreshToken;
}

