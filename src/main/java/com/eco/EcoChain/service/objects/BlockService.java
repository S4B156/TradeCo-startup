package com.eco.EcoChain.service.objects;

import com.eco.EcoChain.entity.Material;
import com.eco.EcoChain.repository.BlockRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import com.eco.EcoChain.entity.Block;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BlockService {
    @Autowired
    private BlockRepository blockRepo;
    ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public void addBlock(Object object) {
        Block block = Block.builder()
                .timestamp(LocalDateTime.now())
                .data(objectMapper.writeValueAsString(object))
                .build();

        block.setPreviousHash(blockRepo.findTopByOrderByBlockIdDesc().orElseThrow().getHash());
        block.setHash(block.calculateHash());
        blockRepo.save(block);
    }

    public boolean isChainValid() {
        List<Block> chain = blockRepo.findAll();
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);
            if (!currentBlock.getHash().equals(currentBlock.calculateHash()) ||
                    !currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
                return false;
            }
        }
        return true;
    }
}
