package com.neo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class QueryResponse {

    @JsonProperty("Neo_ResponseId")
    private String neoResponseId;

    @JsonProperty("Neo_Command")
    private String neoCommand;

    @JsonProperty("Neo_TmnCode")
    private String neoTmnCode;

    @JsonProperty("Neo_TxnRef")
    private String neoTxnRef;

    @JsonProperty("Neo_Amount")
    private Long neoAmount;

    @JsonProperty("Neo_OrderInfo")
    private String neoOrderInfo;

    @JsonProperty("Neo_ResponseCode")
    private String neoResponseCode;

    @JsonProperty("Neo_Message")
    private String neoMessage;

    @JsonProperty("Neo_BankCode")
    private String neoBankCode;

    @JsonProperty("Neo_PayDate")
    private String neoPayDate; // yyyyMMddHHmmss

    @JsonProperty("Neo_TransactionNo")
    private String neoTransactionNo;

    @JsonProperty("Neo_TransactionType")
    private String neoTransactionType; // 01, 02, 03

    @JsonProperty("Neo_TransactionStatus")
    private String neoTransactionStatus;

    @JsonProperty("Neo_PromotionCode")
    private String neoPromotionCode;

    @JsonProperty("Neo_PromotionAmount")
    private Long neoPromotionAmount;

    @JsonProperty("Neo_SecureHash")
    private String neoSecureHash;
}

