package com.aslan.project.bank_rest.repository;

import com.aslan.project.bank_rest.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {}

