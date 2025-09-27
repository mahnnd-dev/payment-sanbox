package com.neo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundRequest {

    @JsonProperty("Neo_RequestId")
    private String neoRequestId;

    @JsonProperty("Neo_Version")
    private String neoVersion;

    @JsonProperty("Neo_Command")
    private String neoCommand;

    @JsonProperty("Neo_TmnCode")
    private String neoTmnCode;

    @JsonProperty("Neo_TransactionType")
    private String neoTransactionType;   // 02 hoặc 03

    @JsonProperty("Neo_TxnRef")
    private String neoTxnRef;

    @JsonProperty("Neo_Amount")
    private Long neoAmount;

    @JsonProperty("Neo_TransactionNo")
    private Long neoTransactionNo;       // tùy chọn

    @JsonProperty("Neo_TransactionDate")
    private String neoTransactionDate;   // yyyyMMddHHmmss

    @JsonProperty("Neo_CreateDate")
    private String neoCreateDate;        // yyyyMMddHHmmss

    @JsonProperty("Neo_IpAddr")
    private String neoIpAddr;

    @JsonProperty("Neo_OrderInfo")
    private String neoOrderInfo;

    @JsonProperty("Neo_SecureHash")
    private String neoSecureHash;
}
