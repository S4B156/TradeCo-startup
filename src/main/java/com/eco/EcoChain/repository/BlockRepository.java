package com.eco.EcoChain.repository;

import com.eco.EcoChain.entity.Block;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block, Long> {
    Optional<Block> findTopByOrderByBlockIdDesc();
    Optional<Block> findTopByOrderByBlockIdAsc();
}
