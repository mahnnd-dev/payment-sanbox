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
        log.info("calculatedHash {}", calculatedHash);
        log.info("requestHash {}", requestHash);
        if (!calculatedHash.equals(requestHash)) {
            log.info("Invalid secure hash");
            return false;
        }
        return true;
    }

    public boolean validateRequestRefundSecureHash(RefundRequest request) {
        String requestHash = request.getNeoSecureHash();
        Map<String, String> fields = new HashMap<>();
        fields.put("Neo_RequestId", request.getNeoRequestId());
        fields.put("Neo_Version", request.getNeoVersion());
        fields.put("Neo_Command", request.getNeoCommand());
        fields.put("Neo_TmnCode", request.getNeoTmnCode());
        fields.put("Neo_TransactionType", request.getNeoTransactionType());
        fields.put("Neo_TxnRef", request.getNeoTxnRef());
        fields.put("Neo_Amount", String.valueOf(request.getNeoAmount()));
        fields.put("Neo_TransactionNo", String.valueOf(request.getNeoTransactionNo()));
        fields.put("Neo_TransactionDate", request.getNeoTransactionDate());
        fields.put("Neo_CreateDate", request.getNeoCreateDate());
        fields.put("Neo_IpAddr", request.getNeoIpAddr());
        fields.put("Neo_OrderInfo", request.getNeoOrderInfo());
        String hashData = NeoUtils.buildQueryString(fields);
        String calculatedHash = NeoUtils.hmacSHA512(secretKey, hashData);
        log.info("calculatedHash {}", calculatedHash);
        log.info("requestHash {}", requestHash);
        if (!calculatedHash.equals(requestHash)) {
            log.info("Invalid secure hash");
            return false;
        }
        return true;
    }

    public boolean validateRequestQuerySecureHash(QueryRequest request) {
        String requestHash = request.getNeoSecureHash();
        Map<String, String> fields = new HashMap<>();
        fields.put("Neo_RequestId", request.getNeoRequestId());
        fields.put("Neo_Version", request.getNeoVersion());
        fields.put("Neo_Command", request.getNeoCommand());
        fields.put("Neo_TmnCode", request.getNeoTmnCode());
        fields.put("Neo_TxnRef", request.getNeoTxnRef());
        fields.put("Neo_OrderInfo", request.getNeoOrderInfo());
        fields.put("Neo_TransactionNo", String.valueOf(request.getNeoTransactionNo()));
        fields.put("Neo_TransactionDate", request.getNeoTransactionDate());
        fields.put("Neo_CreateDate", request.getNeoCreateDate());
        fields.put("Neo_IpAddr", request.getNeoIpAddr());
        String hashData = NeoUtils.buildQueryString(fields);
        String calculatedHash = NeoUtils.hmacSHA512(secretKey, hashData);
        log.info("calculatedHash {}", calculatedHash);
        log.info("requestHash {}", requestHash);
        if (!calculatedHash.equals(requestHash)) {
            log.info("Invalid secure hash");
            return false;
        }
        return true;
    }
}

