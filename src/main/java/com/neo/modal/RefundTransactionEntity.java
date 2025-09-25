package com.neo.modal;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "refund_transaction")
public class RefundTransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String requestId;
    private String version;
    private String command; // always "refund"
    private String tmnCode;
    private String txnRef;
    private String orderInfo;
    private String transactionNo;
    private String transactionDate;
    private String createDate;
    private String ipAddr;
    private Long amount;
    private Long refundAmount;
    private String refundReason;
    private String responseCode;
    private String responseMessage;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

