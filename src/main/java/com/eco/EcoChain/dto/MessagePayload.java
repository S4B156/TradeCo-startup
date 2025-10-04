package com.eco.EcoChain.dto;

import lombok.Data;

@Data
public class MessagePayload {
    private String content;
    private String timestamp;
    private Long senderId;
    private Long chatId;
}
