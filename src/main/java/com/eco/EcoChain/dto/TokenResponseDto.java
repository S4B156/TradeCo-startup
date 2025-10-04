package com.eco.EcoChain.dto;

import lombok.Data;

@Data
public class TokenResponseDto {
    private final String accessToken;

    public TokenResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }
}
