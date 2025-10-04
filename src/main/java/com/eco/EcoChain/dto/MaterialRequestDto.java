package com.eco.EcoChain.dto;

import com.eco.EcoChain.enums.MaterialCategory;
import com.eco.EcoChain.enums.MaterialStatus;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MaterialRequestDto {
    String title;
    String description;
    double quantity;
    String unit;
    String location;
    MaterialCategory category;
}
