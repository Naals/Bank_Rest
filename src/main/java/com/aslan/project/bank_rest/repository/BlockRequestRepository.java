package com.aslan.project.bank_rest.repository;

import com.aslan.project.bank_rest.entity.BlockRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlockRequestRepository extends JpaRepository<BlockRequestEntity, Long> {
    List<BlockRequestEntity> findByUserId(Long userId);
    List<BlockRequestEntity> findByStatus(String status);
}
