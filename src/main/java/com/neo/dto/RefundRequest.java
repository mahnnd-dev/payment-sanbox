package com.neo.dto;

import lombok.Data;

@Data
public class RefundRequest {
    private String Neo_RequestId;
    private String Neo_Version;
    private String Neo_Command;
    private String Neo_TmnCode;
    private String Neo_TransactionType;   // 02 hoặc 03
    private String Neo_TxnRef;
    private Long Neo_Amount;
    private Long Neo_TransactionNo;       // tùy chọn
    private String Neo_TransactionDate;   // yyyyMMddHHmmss
    private String Neo_CreateDate;        // yyyyMMddHHmmss
    private String Neo_IpAddr;
    private String Neo_OrderInfo;
    private String Neo_SecureHash;
}