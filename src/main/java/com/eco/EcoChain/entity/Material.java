package com.eco.EcoChain.entity;

import com.eco.EcoChain.enums.MaterialCategory;
import com.eco.EcoChain.enums.MaterialStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "material")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Material{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "material_id")
    Long materialId;
    String title;
    String description;
    double quantity;
    String unit;
    String location;

    @ElementCollection
    @CollectionTable(name = "material_photos", joinColumns = @JoinColumn(name = "material_id"))
    @Column(name = "photo_url")
    List<String> photos;

    @Enumerated(EnumType.STRING)
    MaterialCategory category;

    @Enumerated(EnumType.STRING)
    MaterialStatus status;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    User supplier;

    @OneToMany(mappedBy = "material")
    List<Deal> deals;

    @Column(name = "posted_at")
    LocalDateTime postedAt;

    @Column(name = "update_at")
    LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = MaterialStatus.AVAILABLE;
        }

        if (postedAt == null) {
            postedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
