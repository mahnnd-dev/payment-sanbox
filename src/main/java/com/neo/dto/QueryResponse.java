package com.neo.dto;

import lombok.Data;

@Data
public class QueryResponse {
    private String neo_ResponseId;
    private String neo_Command;
    private String neo_TmnCode;
    private String neo_TxnRef;
    private Long neo_Amount;
    private String neo_OrderInfo;
    private String neo_ResponseCode;
    private String neo_Message;
    private String neo_BankCode;
    private String neo_PayDate;             // yyyyMMddHHmmss
    private Long neo_TransactionNo;
    private String neo_TransactionType;     // 01, 02, 03
    private String neo_TransactionStatus;
    private String neo_PromotionCode;
    private Long neo_PromotionAmount;
    private String neo_SecureHash;
}
