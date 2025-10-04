package com.eco.EcoChain.dto;

import lombok.Data;

@Data
public class AuthenticationResponseDto {
    private final String accessToken;
    private final String refreshToken;

    public AuthenticationResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}