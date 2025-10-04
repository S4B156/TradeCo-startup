package com.eco.EcoChain.repository;

import com.eco.EcoChain.entity.Material;
import com.eco.EcoChain.entity.Request;
import com.eco.EcoChain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByMaterialIn(List<Material> materials);
    List<Request> findAllByRequester(User requester);
}
