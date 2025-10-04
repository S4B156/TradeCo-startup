package com.eco.EcoChain.mappers;

import com.eco.EcoChain.dto.ResponseDealDto;
import com.eco.EcoChain.entity.Deal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DealMapper {
    @Mapping(source = "supplier.userId", target = "supplierId")
    @Mapping(source = "supplier.username", target = "supplierName")
    @Mapping(source = "consumer.userId", target = "consumerId")
    @Mapping(source = "consumer.username", target = "consumerName")
    @Mapping(source = "material.title", target = "materialName")
    @Mapping(source = "chat.chatId", target = "chatId")
    ResponseDealDto toDto(Deal deal);
}

