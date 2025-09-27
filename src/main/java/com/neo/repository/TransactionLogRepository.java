package com.neo.repository;

import com.neo.modal.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionLogRepository extends JpaRepository<TransactionLog, Long> {
    List<TransactionLog> findAllByTxnRef(String txnRef);
}
