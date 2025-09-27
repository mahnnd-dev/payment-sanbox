package com.neo.repository;

import com.neo.modal.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionLogRepository extends JpaRepository<TransactionLog, Long> {
    TransactionLog findAllByTxnRef(String txnRef);

    TransactionLog findAllByTxnRefAndTransactionStatus(String txnRef, String transactionStatus);
}
