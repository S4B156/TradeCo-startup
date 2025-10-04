package com.eco.EcoChain.repository;

import com.eco.EcoChain.entity.Material;
import com.eco.EcoChain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findBySupplier(User supplier);
}
