package com.eco.EcoChain.dto;

import com.eco.EcoChain.enums.DealStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UpdateDealRequest {
    private DealStatus status;
    private BigDecimal price;
    private LocalDateTime acceptedAt;
    private LocalDateTime completedAt;
}