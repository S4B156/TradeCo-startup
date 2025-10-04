package com.eco.EcoChain.repository;

import com.eco.EcoChain.entity.Chat;
import com.eco.EcoChain.entity.Message;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByChat(Chat chat);
    Page<Message> findAllByChat(Chat chat, Pageable pageable);
}
