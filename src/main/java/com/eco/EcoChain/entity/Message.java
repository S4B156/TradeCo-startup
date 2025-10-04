package com.eco.EcoChain.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "message")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    Long messageId;

    String content;
    LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    User sender;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    Chat chat;
}
