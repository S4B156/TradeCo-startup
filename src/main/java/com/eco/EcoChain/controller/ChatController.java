package com.eco.EcoChain.controller;

import com.eco.EcoChain.dto.ChatMessageDTO;
import com.eco.EcoChain.dto.MessagePayload;
import com.eco.EcoChain.entity.Chat;
import com.eco.EcoChain.entity.Message;
import com.eco.EcoChain.entity.User;
import com.eco.EcoChain.service.objects.ChatService;
import com.eco.EcoChain.service.objects.MessageService;
import com.eco.EcoChain.service.objects.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
public class ChatController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private ChatService chatService;
    @Autowired
    private UserService userService;
    @Autowired
    private MessageService messageService;

    @MessageMapping("/chat/{chatId}")
    public void sendMessage(@DestinationVariable Long chatId, @Payload MessagePayload payload, Principal principal) {
        String username = principal.getName();
        User sender = userService.loadUserByUsername(username);

        Chat chat = chatService.getChatById(chatId);
        if (!chat.getUser1().getUserId().equals(sender.getUserId()) &&
                !chat.getUser2().getUserId().equals(sender.getUserId())) {
            throw new SecurityException("Access denied to this chat");
        }

        Message message = Message.builder()
                .content(payload.getContent())
                .timestamp(LocalDateTime.now())
                .sender(sender)
                .chat(chat)
                .build();
        message = messageService.save(message);

        ChatMessageDTO messageDTO = new ChatMessageDTO();
        messageDTO.setId(message.getMessageId());
        messageDTO.setContent(message.getContent());
        messageDTO.setTimestamp(message.getTimestamp());
        messageDTO.setSenderId(sender.getUserId());
        messageDTO.setSenderUsername(sender.getUsername());
        messageDTO.setChatId(chat.getChatId());

        simpMessagingTemplate.convertAndSend("/topic/chat/" + chatId, messageDTO);
    }
}