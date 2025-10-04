package com.eco.EcoChain.repository;

import com.eco.EcoChain.entity.Deal;
import com.eco.EcoChain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DealRepository extends JpaRepository<Deal, Long> {
    @Query("SELECT d FROM Deal d WHERE d.supplier = :user OR d.consumer = :user")
    List<Deal> findAllByUser(@Param("user") User user);
    Optional<Deal> findByChatChatId(Long chatId);
}
