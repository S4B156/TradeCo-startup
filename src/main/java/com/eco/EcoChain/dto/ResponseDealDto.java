package com.eco.EcoChain.dto;

import com.eco.EcoChain.enums.DealStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ResponseDealDto {
    Long dealId;

    String materialName;

    Long supplierId;
    String supplierName;

    Long consumerId;
    String consumerName;

    BigDecimal price;
    DealStatus status;

    LocalDateTime createdAt;
    LocalDateTime acceptedAt;
    LocalDateTime completedAt;

    Long chatId;
}
