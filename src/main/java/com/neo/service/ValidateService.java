package com.neo.service;

import com.neo.dto.NeoPaymentRequest;
import com.neo.dto.QueryRequest;
import com.neo.dto.RefundRequest;
import com.neo.util.NeoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ValidateService {
    @Value("${neo.payment.secret-key}")
    private String secretKey;

    public boolean validateRequestSecureHash(NeoPaymentRequest request) {
        String requestHash = request.getNeo_SecureHash();
        Map<String, String> fields = new HashMap<>();
        fields.put("Neo_Amount", request.getNeo_Amount());
        fields.put("Neo_Command", request.getNeo_Command());
        fields.put("Neo_CreateDate", request.getNeo_CreateDate());
        fields.put("Neo_CurrCode", request.getNeo_CurrCode());
        fields.put("Neo_ExpireDate", request.getNeo_ExpireDate());
        fields.put("Neo_IpAddr", request.getNeo_IpAddr());
        fields.put("Neo_Locale", request.getNeo_Locale());
        fields.put("Neo_OrderInfo", request.getNeo_OrderInfo());
        fields.put("Neo_OrderType", request.getNeo_OrderType());
        fields.put("Neo_ReturnUrl", request.getNeo_ReturnUrl());
        fields.put("Neo_TmnCode", request.getNeo_TmnCode());
        fields.put("Neo_TxnRef", request.getNeo_TxnRef());
        fields.put("Neo_Version", request.getNeo_Version());
        String hashData = NeoUtils.buildQueryString(fields);
        String calculatedHash = NeoUtils.hmacSHA512(secretKey, hashData);
        if (!calculatedHash.equals(requestHash)) {
            log.info("Invalid secure hash");
            return false;
        }
        return true;
    }

    public boolean validateRequestRefundSecureHash(RefundRequest request) {
        String requestHash = request.getNeo_SecureHash();
        Map<String, String> fields = new HashMap<>();
        fields.put("Neo_RequestId", request.getNeo_RequestId());
        fields.put("Neo_Version", request.getNeo_Version());
        fields.put("Neo_Command", request.getNeo_Command());
        fields.put("Neo_TmnCode", request.getNeo_TmnCode());
        fields.put("Neo_TransactionType", request.getNeo_TransactionType());
        fields.put("Neo_TxnRef", request.getNeo_TxnRef());
        fields.put("Neo_Amount", String.valueOf(request.getNeo_Amount()));
        fields.put("Neo_TransactionNo", String.valueOf(request.getNeo_TransactionNo()));
        fields.put("Neo_TransactionDate", request.getNeo_TransactionDate());
        fields.put("Neo_CreateDate", request.getNeo_CreateDate());
        fields.put("Neo_IpAddr", request.getNeo_IpAddr());
        fields.put("Neo_OrderInfo", request.getNeo_OrderInfo());
        String hashData = NeoUtils.buildQueryString(fields);
        String calculatedHash = NeoUtils.hmacSHA512(secretKey, hashData);
        if (!calculatedHash.equals(requestHash)) {
            log.info("Invalid secure hash");
            return false;
        }
        return true;
    }

    public boolean validateRequestQuerySecureHash(QueryRequest request) {
        String requestHash = request.getNeo_SecureHash();
        Map<String, String> fields = new HashMap<>();
        fields.put("Neo_RequestId", request.getNeo_RequestId());
        fields.put("Neo_Version", request.getNeo_Version());
        fields.put("Neo_Command", request.getNeo_Command());
        fields.put("Neo_TmnCode", request.getNeo_TmnCode());
        fields.put("Neo_TxnRef", request.getNeo_TxnRef());
        fields.put("Neo_OrderInfo", request.getNeo_OrderInfo());
        fields.put("Neo_TransactionNo", String.valueOf(request.getNeo_TransactionNo()));
        fields.put("Neo_TransactionDate", request.getNeo_TransactionDate());
        fields.put("Neo_CreateDate", request.getNeo_CreateDate());
        fields.put("Neo_IpAddr", request.getNeo_IpAddr());
        String hashData = NeoUtils.buildQueryString(fields);
        String calculatedHash = NeoUtils.hmacSHA512(secretKey, hashData);
        if (!calculatedHash.equals(requestHash)) {
            log.info("Invalid secure hash");
            return false;
        }
        return true;
    }
}

