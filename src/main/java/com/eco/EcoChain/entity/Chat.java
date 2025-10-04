package com.eco.EcoChain.entity;

import com.eco.EcoChain.service.objects.UserService;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "chat")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    Long chatId;

    @ManyToOne
    @JoinColumn(name = "user1_id")
    User user1;

    @ManyToOne
    @JoinColumn(name = "user2_id")
    User user2;

    @OneToMany(mappedBy = "chat")
    List<Message> messages;

    @OneToOne(mappedBy = "chat")
    Deal deal;

    public User getOtherUser(Long userId){
        return Objects.equals(this.user1.getUserId(), userId) ? user2 : user1;
    }
}
