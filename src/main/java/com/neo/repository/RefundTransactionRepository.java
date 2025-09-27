package com.neo.repository;

import com.neo.modal.RefundTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundTransactionRepository extends JpaRepository<RefundTransaction, Long> {
}
