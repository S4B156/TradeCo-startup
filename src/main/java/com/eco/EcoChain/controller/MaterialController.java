package com.eco.EcoChain.controller;

import com.eco.EcoChain.dto.MaterialRequestDto;
import com.eco.EcoChain.dto.MaterialResponseDto;
import com.eco.EcoChain.entity.Material;
import com.eco.EcoChain.entity.User;
import com.eco.EcoChain.mappers.MaterialMapper;
import com.eco.EcoChain.service.objects.BlockService;
import com.eco.EcoChain.service.objects.MaterialService;
import com.eco.EcoChain.service.objects.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/materials")
public class MaterialController {
    @Autowired
    private MaterialService materialService;
    @Autowired
    private UserService userService;
    @Autowired
    private MaterialMapper materialMapper;
    @Autowired
    private BlockService blockService;

    @GetMapping
    public List<Material> getAll(){
        return materialService.getAllMaterials();
    }

    @GetMapping("/{id}")
    public Material getById(@PathVariable Long materialId) {
        return materialService.getMaterialById(materialId);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my")
    public ResponseEntity<List<Material>> getUserMaterials(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.loadUserByUsername(userDetails.getUsername());
        return ResponseEntity.ok(user.getMaterials());
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<MaterialResponseDto> save(@RequestBody MaterialRequestDto material,
                                                    @RequestParam("photos") MultipartFile[] files,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        Material response = materialService.saveMaterial(material, files, username);

        blockService.addBlock(materialMapper.toDto(response));

        return ResponseEntity.status(HttpStatus.CREATED).body(materialMapper.toDto(response));
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/{id}")
    public ResponseEntity<MaterialResponseDto> patchMaterial(@PathVariable Long materialId,
                                                     @RequestBody MaterialRequestDto materialDto,
                                                     @AuthenticationPrincipal UserDetails userDetails){
        String username = userDetails.getUsername();
        Material material = materialService.patchMaterial(materialId, materialDto, username);
        return ResponseEntity.ok(materialMapper.toDto(material));
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{id}")
    public void deleteMaterial(@PathVariable Long materialId, @AuthenticationPrincipal UserDetails userDetails){
        String username = userDetails.getUsername();
        materialService.deleteMaterial(materialId, username);
    }

    /*
    @GetMapping
    public Page<MaterialDoc> listMaterials(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return materialDocRepository.findAll(pageable);
    }

    @GetMapping("/search")
    public Page<MaterialDoc> search(
            @RequestParam(value = "query", required = false, defaultValue = "") String query,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return materialDocRepository.searchByQuery(query, pageable);
    }
    */
}
