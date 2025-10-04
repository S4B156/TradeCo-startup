package com.eco.EcoChain.entity;

import com.eco.EcoChain.enums.DealStatus;
import com.eco.EcoChain.enums.MaterialStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "deal")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Deal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deal_id")
    Long dealId;

    @ManyToOne
    @JoinColumn(name = "material_id")
    Material material;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    User supplier;

    @ManyToOne
    @JoinColumn(name = "consumer_id")
    User consumer;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    DealStatus status;

    @Column(name = "price")
    BigDecimal price;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "chat_id")
    Chat chat;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "accepted_at")
    LocalDateTime acceptedAt;

    @Column(name = "completed_at")
    LocalDateTime completedAt;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = DealStatus.CREATED;
        }

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
