package com.neo.repository;

import com.neo.modal.RefundTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundTransactionRepository extends JpaRepository<RefundTransactionEntity, Long> {
}
