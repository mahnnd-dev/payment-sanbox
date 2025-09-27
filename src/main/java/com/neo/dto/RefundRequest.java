package com.neo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

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

    public Map<String, Object> toMap(RefundRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put("Neo_RequestId", request.getNeoRequestId());
        map.put("Neo_Version", request.getNeoVersion());
        map.put("Neo_Command", request.getNeoCommand());
        map.put("Neo_TmnCode", request.getNeoTmnCode());
        map.put("Neo_TransactionType", request.getNeoTransactionType());
        map.put("Neo_TxnRef", request.getNeoTxnRef());
        map.put("Neo_Amount", request.getNeoAmount());
        map.put("Neo_TransactionNo", request.getNeoTransactionNo());
        map.put("Neo_TransactionDate", request.getNeoTransactionDate());
        map.put("Neo_CreateDate", request.getNeoCreateDate());
        map.put("Neo_IpAddr", request.getNeoIpAddr());
        map.put("Neo_OrderInfo", request.getNeoOrderInfo());
        map.put("Neo_SecureHash", request.getNeoSecureHash());
        return map;
    }

}
