package com.eco.EcoChain.controller;

import com.eco.EcoChain.dto.ResponseDealDto;
import com.eco.EcoChain.dto.UpdateDealRequest;
import com.eco.EcoChain.entity.Chat;
import com.eco.EcoChain.entity.Deal;
import com.eco.EcoChain.entity.Material;
import com.eco.EcoChain.entity.User;
import com.eco.EcoChain.enums.DealStatus;
import com.eco.EcoChain.mappers.DealMapper;
import com.eco.EcoChain.repository.DealRepository;
import com.eco.EcoChain.service.objects.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/deals")
public class DealController {
    @Autowired
    private DealService dealService;
    @Autowired
    private UserService userService;
    @Autowired
    private DealRepository dealRepo;
    @Autowired
    private DealMapper dealMapper;
    @Autowired
    private BlockService blockService;

    @ModelAttribute("currentUser")
    public User getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        return userService.loadUserByUsername(userDetails.getUsername());
    }

    @GetMapping("/{dealId}")
    public ResponseEntity<ResponseDealDto> getDealById(
            @PathVariable Long dealId,
            @ModelAttribute("currentUser") User user) {
        Deal deal = dealService.getDealIfUserHasAccess(dealId, user.getUserId());
        return ResponseEntity.ok(dealMapper.toDto(deal));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ResponseDealDto>> getDealById(@ModelAttribute("currentUser") User user) {
        List<Deal> deals = dealService.getAllDeals(user);
        List<ResponseDealDto> responseDeals = deals.stream()
                .map(dealMapper::toDto)
                .toList();
        return ResponseEntity.ok(responseDeals);
    }
    // Обе стороны согласились, условия сделки подтверждены (из чата или вручную).
    @PostMapping("/{dealId}/confirm")
    public void confirmDeal(@PathVariable Long dealId,
                            @ModelAttribute("currentUser") User user){
        Deal deal = dealService.getDealIfUserHasAccess(dealId, user.getUserId());

        deal.setAcceptedAt(LocalDateTime.now());
        deal.setStatus(DealStatus.CONFIRMED);
    }

    // Сделка успешно завершена
    @PostMapping("/{dealId}/complete")
    public void completeDeal(@PathVariable Long dealId,
                             @ModelAttribute("currentUser") User user){
        Deal deal = dealService.getDealIfUserHasAccess(dealId, user.getUserId());

        deal.setCompletedAt(LocalDateTime.now());
        deal.setStatus(DealStatus.COMPLETED);

        blockService.addBlock(dealMapper.toDto(deal));
    }

    // Сделка отменена одной из сторон
    @PostMapping("/{dealId}/cancel")
    public void cancelDeal(@PathVariable Long dealId,
                           @ModelAttribute("currentUser") User user){
        Deal deal = dealService.getDealIfUserHasAccess(dealId, user.getUserId());

        deal.setStatus(DealStatus.CANCELLED);
    }

    // Материалы поставляются, сделка выполняется
    @PostMapping("/{dealId}/start")
    public void startDeal(@PathVariable Long dealId,
                          @ModelAttribute("currentUser") User user){
        Deal deal = dealService.getDealIfUserHasAccess(dealId, user.getUserId());

        if (deal.getStatus() != DealStatus.CONFIRMED) {
            throw new IllegalStateException("Сделку можно начать только после подтверждения.");
        }

        deal.setStatus(DealStatus.IN_PROGRESS);
    }

    @GetMapping("/by-chat/{chatId}")
    public ResponseEntity<Deal> getDealByChatId(
            @PathVariable Long chatId,
            @ModelAttribute("currentUser") User user) {
        Deal deal = dealService.getDealByChatId(chatId);

        if (deal == null) {
            return ResponseEntity.ok().build();
        }

        if (!deal.getSupplier().getUserId().equals(user.getUserId()) &&
                !deal.getConsumer().getUserId().equals(user.getUserId())) {
            throw new SecurityException("Access denied to this deal");
        }

        return ResponseEntity.ok(deal);
    }

    @PatchMapping("/{dealId}")
    public ResponseEntity<Deal> updateDeal(
            @PathVariable Long dealId,
            @RequestBody UpdateDealRequest request,
            @ModelAttribute("currentUser") User user) {
        Deal deal = dealService.getDealIfUserHasAccess(dealId, user.getUserId());

        Optional.ofNullable(request.getStatus()).ifPresent(deal::setStatus);
        Optional.ofNullable(request.getPrice()).ifPresent(deal::setPrice);
        Optional.ofNullable(request.getAcceptedAt()).ifPresent(deal::setAcceptedAt);
        Optional.ofNullable(request.getCompletedAt()).ifPresent(deal::setCompletedAt);

        dealRepo.save(deal);
        return ResponseEntity.ok(deal);
    }

    @DeleteMapping("/{dealId}")
    public ResponseEntity<Void> deleteDeal(
            @PathVariable Long dealId,
            @ModelAttribute("currentUser") User user) {
        Deal deal = dealService.getDealIfUserHasAccess(dealId, user.getUserId());

        if (deal.getStatus() != DealStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot delete deal in " + deal.getStatus() + " status");
        }

        deal.setChat(null);
        dealRepo.save(deal);
        dealRepo.delete(deal);
        return ResponseEntity.noContent().build();
    }
}
