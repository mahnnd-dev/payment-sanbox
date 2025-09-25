package com.neo.repository;

import com.neo.modal.QueryTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueryTransactionRepository extends JpaRepository<QueryTransactionEntity, Long> {
}
