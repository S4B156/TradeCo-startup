package com.eco.EcoChain.service.objects;

import com.eco.EcoChain.dto.MaterialRequestDto;
import com.eco.EcoChain.entity.Material;
import com.eco.EcoChain.mappers.MaterialMapper;
import com.eco.EcoChain.repository.MaterialRepository;
import com.eco.EcoChain.service.S3Service;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class MaterialService {
    @Autowired
    private S3Service s3Service;
    @Autowired
    private MaterialRepository materialRepo;
    @Autowired
    private UserService userService;
    /*@Autowired
    private MaterialDocRepository materialDocRepository;
    @Autowired
    private BlockService blockService;*/
    @Autowired
    private MaterialMapper materialMapper;

    public List<Material> getAllMaterials(){
        return materialRepo.findAll();
    }

    public Material saveMaterial(MaterialRequestDto materialDto, MultipartFile[] files, String username){
        List<String> photoUrls = Arrays.stream(files)
                .map(s3Service::uploadFile)
                .toList();

        Material material = materialMapper.toEntity(materialDto);
        return materialRepo.save(material);
        // blockService.addBlockAsMaterial(material);
        // materialDocRepository.save(MaterialDoc.toDoc(material));
    }

    @Transactional

    public Material patchMaterial(Long id, MaterialRequestDto materialDto, String username) {
        Material material = getMaterialById(id);
        checkMaterialSupplier(material, username);

        Optional.ofNullable(materialDto.getTitle()).ifPresent(material::setTitle);
        Optional.ofNullable(materialDto.getDescription()).ifPresent(material::setDescription);
        Optional.of(materialDto.getQuantity()).ifPresent(material::setQuantity);
        Optional.ofNullable(materialDto.getUnit()).ifPresent(material::setUnit);
        Optional.ofNullable(materialDto.getLocation()).ifPresent(material::setLocation);
        Optional.ofNullable(materialDto.getCategory()).ifPresent(material::setCategory);

        return material;
    }

    public void deleteMaterial(Long id, String username){
        Material material = getMaterialById(id);
        checkMaterialSupplier(material, username);

        materialRepo.delete(material);
    }

    @SneakyThrows
    public void checkMaterialSupplier(Material material, String username){
        if(!material.getSupplier().getUsername().equals(username)){
            throw new AccessDeniedException("You are not the owner of this material");
        }
    }

    public Material getMaterialById(Long id){
        return materialRepo.findById(id).orElseThrow(() -> new RuntimeException("Material not found"));
    }
}
