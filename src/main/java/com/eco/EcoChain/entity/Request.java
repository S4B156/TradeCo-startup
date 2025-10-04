package com.eco.EcoChain.entity;

import com.eco.EcoChain.enums.MaterialStatus;
import com.eco.EcoChain.enums.RequestStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "request")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    Long requestId;

    @ManyToOne
    @JoinColumn(name = "material_id")
    Material material;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    User requester;

    String message;

    @Enumerated(EnumType.STRING)
    RequestStatus status; // PENDING, APPROVED, DECLINED

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "update_at")
    LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = RequestStatus.APPROVED ;
        }

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
