package com.neo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IPNRequest {
    private String Neo_TmnCode;
    private String Neo_Amount;
    private String Neo_BankCode;
    private String Neo_BankTranNo;
    private String Neo_CardType;
    private String Neo_PayDate;
    private String Neo_OrderInfo;
    private String Neo_TransactionNo;
    private String Neo_ResponseCode;
    private String Neo_TransactionStatus;
    private String Neo_TxnRef;
    private String Neo_SecureHash;

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("Neo_TmnCode", getNeo_TmnCode());
        map.put("Neo_Amount", getNeo_Amount());
        map.put("Neo_BankCode", getNeo_BankCode());
        map.put("Neo_BankTranNo", getNeo_BankTranNo());
        map.put("Neo_CardType", getNeo_CardType());
        map.put("Neo_PayDate", getNeo_PayDate());
        map.put("Neo_OrderInfo", getNeo_OrderInfo());
        map.put("Neo_TransactionNo", getNeo_TransactionNo());
        map.put("Neo_ResponseCode", getNeo_ResponseCode());
        map.put("Neo_TransactionStatus", getNeo_TransactionStatus());
        map.put("Neo_TxnRef", getNeo_TxnRef());
        map.put("Neo_SecureHash", getNeo_SecureHash());
        return map;
    }

}