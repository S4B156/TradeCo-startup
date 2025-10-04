package com.eco.EcoChain.service.objects;

import com.eco.EcoChain.entity.Message;
import com.eco.EcoChain.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepo;
    @Autowired
    private ChatService chatService;

    public Message save(Message message){
        return messageRepo.save(message);
    }
    public List<Message> getMessagesByChatId(Long chatId){
        return messageRepo.findAllByChat(chatService.getChatById(chatId));
    }
    public Page<Message> getMessagesByChatId(Long chatId, Pageable pageable) {
        return messageRepo.findAllByChat(chatService.getChatById(chatId), pageable);
    }
}
