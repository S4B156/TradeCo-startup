package com.eco.EcoChain.service.objects;

import com.eco.EcoChain.entity.*;
import com.eco.EcoChain.enums.DealStatus;
import com.eco.EcoChain.repository.DealRepository;
import com.eco.EcoChain.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DealService {
    @Autowired
    private DealRepository dealRepo;
    @Autowired
    private UserService userService;
    @Autowired
    private RequestRepository requestRepo;

    public List<Deal> getUserDeals(String username){
        return dealRepo.findAllByUser(userService.loadUserByUsername(username));
    }

    public Deal getDealIfUserHasAccess(Long dealId, Long userId) {
        Deal deal = dealRepo.findById(dealId)
                .orElseThrow(() -> new RuntimeException("Сделка не найдена"));

        boolean isSupplier = deal.getSupplier().getUserId().equals(userId);
        boolean isConsumer = deal.getConsumer().getUserId().equals(userId);

        if (!isSupplier && !isConsumer) {
            throw new AccessDeniedException("Нет доступа к сделке");
        }

        return deal;
    }

    public List<Deal> getAllDeals(User user) {
        return dealRepo.findAllByUser(user);
    }

    public void createNewDeal(Material material, User consumer, User supplier, Chat chat){
        Deal deal = Deal.builder()
                .material(material)
                .supplier(supplier)
                .consumer(consumer)
                .chat(chat)
                .build();
        dealRepo.save(deal);
    }

    public Deal getDealByChatId(Long chatId) {
        return dealRepo.findByChatChatId(chatId).orElse(null);
    }
}
