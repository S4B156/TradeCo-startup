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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chats")
public class ChatRestController {
    @Autowired
    private ChatService chatService;
    @Autowired
    private UserService userService;
    @Autowired
    private MessageService messageService;

    @GetMapping
    public ResponseEntity<List<Chat>> getUserChats(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.loadUserByUsername(userDetails.getUsername());
        return ResponseEntity.ok(user.getAllChats());
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<ChatMessageDTO>> getMessages(
            @PathVariable Long chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.loadUserByUsername(userDetails.getUsername());
        Chat chat = chatService.getChatById(chatId);
        if (!chat.getUser1().getUserId().equals(user.getUserId()) &&
                !chat.getUser2().getUserId().equals(user.getUserId())) {
            throw new SecurityException("Access denied to this chat");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messagePage = messageService.getMessagesByChatId(chatId, pageable);
        List<ChatMessageDTO> messages = messagePage.getContent().stream()
                .map(message -> {
                    ChatMessageDTO dto = new ChatMessageDTO();
                    dto.setId(message.getMessageId());
                    dto.setContent(message.getContent());
                    dto.setTimestamp(message.getTimestamp());
                    dto.setSenderId(message.getSender().getUserId());
                    dto.setSenderUsername(message.getSender().getUsername());
                    dto.setChatId(chatId);
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(messages);
    }
}
