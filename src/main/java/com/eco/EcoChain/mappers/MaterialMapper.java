package com.eco.EcoChain.mappers;

import com.eco.EcoChain.dto.MaterialRequestDto;
import com.eco.EcoChain.dto.MaterialResponseDto;
import com.eco.EcoChain.entity.Material;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MaterialMapper {
    Material toEntity(MaterialRequestDto materialDto);

    @Mapping(source = "supplier.userId", target = "supplierId")
    MaterialResponseDto toDto(Material material);
}
