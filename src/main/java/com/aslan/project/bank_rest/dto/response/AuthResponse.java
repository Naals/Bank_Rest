package com.aslan.project.bank_rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
}

