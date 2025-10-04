package com.eco.EcoChain.dto;

import com.eco.EcoChain.enums.MaterialCategory;
import com.eco.EcoChain.enums.MaterialStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MaterialResponseDto{
    Long materialId;
    String title;
    String description;
    double quantity;
    String unit;
    String location;
    Long supplierId;
    MaterialCategory category;
    MaterialStatus status;
    LocalDateTime postedAt;
}