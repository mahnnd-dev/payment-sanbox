package com.neo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class QueryRequest {

    // Bắt buộc
    @JsonProperty("Neo_RequestId")
    private String neoRequestId;

    @JsonProperty("Neo_Version")
    private String neoVersion;

    @JsonProperty("Neo_Command")
    private String neoCommand;

    @JsonProperty("Neo_TmnCode")
    private String neoTmnCode;

    @JsonProperty("Neo_TxnRef")
    private String neoTxnRef;

    @JsonProperty("Neo_OrderInfo")
    private String neoOrderInfo;
    // Tuỳ chọn
    @JsonProperty("Neo_TransactionNo")
    private String neoTransactionNo;

    @JsonProperty("Neo_TransactionDate")
    private String neoTransactionDate;

    @JsonProperty("Neo_CreateDate")
    private String neoCreateDate;

    @JsonProperty("Neo_IpAddr")
    private String neoIpAddr;

    @JsonProperty("Neo_SecureHash")
    private String neoSecureHash;
}
