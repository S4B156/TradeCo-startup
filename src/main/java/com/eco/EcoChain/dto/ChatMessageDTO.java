package com.eco.EcoChain.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageDTO {
    private Long id;
    private String content;
    private LocalDateTime timestamp;
    private Long senderId;
    private String senderUsername;
    private Long chatId;
}
