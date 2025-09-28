package com.neo.modal;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "transaction_log")
public class TransactionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Trường phân loại ---
    @Column(nullable = false)
    private String command; // "querydr", "refund"

    // --- Các trường chung ---
    @Column(nullable = false, unique = true)
    private String requestId;
    private String version;
    @Column(nullable = false)
    private String tmnCode;
    @Column(nullable = false)
    private String txnRef;
    private String orderInfo;
    @Column(nullable = false)
    private String transactionDate;
    @Column(nullable = false)
    private String createDate;
    @Column(nullable = false)
    private String ipAddr;
    private String responseCode;
    @Column(columnDefinition = "VARCHAR(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String responseMessage;
    private String transactionNo;
    private String payDate;
    private String currCode;
    private String locale;
    private String orderType;
    private String expireDate;
    private String bankName;
    private String cardNumber;
    private String cardHolder;
    private String cardDate;
    // --- Các trường riêng cho REFUND ---
    private String transactionType; // "02", "03"
    private Long refundAmount; // Số tiền hoàn
    private String refundReason; // Số tiền hoàn

    // --- Các trường riêng cho QUERY ---
    private String transactionStatus; // Trạng thái giao dịch gốc
    private Long amount; // Số tiền giao dịch gốc
    private String bankCode; // Mã ngân hàng
}

