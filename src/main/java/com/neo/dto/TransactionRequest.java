package com.neo.dto;

import lombok.Data;

@Data
public class TransactionRequest {
    private String requestId;
    private String version;
    private String command;
    private String tmnCode;
    private String txnRef;
    private String orderInfo;
    private String transactionNo;
    private String transactionDate;
    private String createDate;
    private String ipAddr;
    private Long amount;
    private Long amountPay;
    private String bankCode;
    private String bankName;
    private String cardNumber;
    private String cardHolder;
    private String cardDate;
    private Long refundAmount;
    private String refundReason;
    private String status;
    private String statusMessage;
    private String secureHash;
}