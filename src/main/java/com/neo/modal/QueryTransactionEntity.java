package com.neo.modal;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "query_transaction")
public class QueryTransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String requestId;
    private String version;
    private String command; // always "querydr"
    private String tmnCode;
    private String txnRef;
    private String orderInfo;
    private String transactionNo;
    private String transactionDate;
    private String createDate;
    private String ipAddr;
    private String responseCode;
    private String responseMessage;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
