package com.neo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

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

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("Neo_RequestId", getNeoRequestId());
        map.put("Neo_Version", getNeoVersion());
        map.put("Neo_Command", getNeoCommand());
        map.put("Neo_TmnCode", getNeoTmnCode());
        map.put("Neo_TxnRef", getNeoTxnRef());
        map.put("Neo_OrderInfo", getNeoOrderInfo());
        map.put("Neo_TransactionNo", getNeoTransactionNo());
        map.put("Neo_TransactionDate", getNeoTransactionDate());
        map.put("Neo_CreateDate", getNeoCreateDate());
        map.put("Neo_IpAddr", getNeoIpAddr());
        map.put("Neo_SecureHash", getNeoSecureHash());
        return map;
    }
}
