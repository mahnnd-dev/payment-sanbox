package com.neo.dto;

import lombok.Data;

@Data
public class RefundRequest {
    private String neo_RequestId;
    private String neo_Version;
    private String neo_Command;
    private String neo_TmnCode;
    private String neo_TransactionType;   // 02 hoặc 03
    private String neo_TxnRef;
    private Long neo_Amount;
    private Long neo_TransactionNo;       // tùy chọn
    private String neo_TransactionDate;   // yyyyMMddHHmmss
    private String neo_CreateBy;
    private String neo_CreateDate;        // yyyyMMddHHmmss
    private String neo_IpAddr;
    private String neo_OrderInfo;
    private String neo_SecureHash;
}