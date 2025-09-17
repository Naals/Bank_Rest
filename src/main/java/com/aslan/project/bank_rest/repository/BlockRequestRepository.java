package com.aslan.project.bank_rest.repository;

import com.aslan.project.bank_rest.entity.BlockRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BlockRequestRepository extends JpaRepository<BlockRequestEntity, Long> {
}
