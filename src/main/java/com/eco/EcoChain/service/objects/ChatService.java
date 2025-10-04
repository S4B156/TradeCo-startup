package com.eco.EcoChain.service.objects;

import com.eco.EcoChain.entity.Chat;
import com.eco.EcoChain.entity.User;
import com.eco.EcoChain.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    @Autowired
    private ChatRepository chatRepo;

    public Chat getChatById(Long chatId){
        return chatRepo.findById(chatId).orElseThrow();
    }

    public Chat save(User user1, User user2){
        Chat chat = Chat.builder()
                .user1(user1)
                .user2(user2)
                .build();
        return chatRepo.save(chat);
    }
}
