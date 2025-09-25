package com.neo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Test controller để mô phỏng merchant nhận IPN callback
 * Chỉ dùng để test, trong thực tế merchant sẽ có endpoint riêng
 */
@Slf4j
@RestController
@RequestMapping("/test/ipn")
public class IPNTestController {

    @Value("${neo.payment.secret-key:your-secret-key}")
    private String secretKey;

    /**
     * Endpoint mô phỏng merchant nhận IPN callback từ Neo Payment Gateway
     * GET /test/ipn/callback?Neo_TmnCode=xxx&Neo_Amount=xxx&...
     */
    @GetMapping("/callback")
    public String handleIPNCallback(
            @RequestParam(name = "Neo_TmnCode") String tmnCode,
            @RequestParam(name = "Neo_Amount") String amount,
            @RequestParam(name = "Neo_BankCode") String bankCode,
            @RequestParam(name = "Neo_BankTranNo", required = false) String bankTranNo,
            @RequestParam(name = "Neo_CardType", required = false) String cardType,
            @RequestParam(name = "Neo_PayDate", required = false) String payDate,
            @RequestParam(name = "Neo_OrderInfo") String orderInfo,
            @RequestParam(name = "Neo_TransactionNo") String transactionNo,
            @RequestParam(name = "Neo_ResponseCode") String responseCode,
            @RequestParam(name = "Neo_TransactionStatus") String transactionStatus,
            @RequestParam(name = "Neo_TxnRef") String txnRef,
            @RequestParam(name = "Neo_SecureHash") String secureHash) {
        
        log.info("=== IPN Callback Received ===");
        log.info("TmnCode: {}", tmnCode);
        log.info("Amount: {}", amount);
        log.info("BankCode: {}", bankCode);
        log.info("TxnRef: {}", txnRef);
        log.info("ResponseCode: {}", responseCode);
        log.info("TransactionStatus: {}", transactionStatus);
        log.info("SecureHash: {}", secureHash);
        
        try {
            // Validate secure hash
            Map<String, String> params = new LinkedHashMap<>();
            params.put("Neo_Amount", amount);
            params.put("Neo_BankCode", bankCode);
            if (bankTranNo != null) params.put("Neo_BankTranNo", bankTranNo);
            if (cardType != null) params.put("Neo_CardType", cardType);
            params.put("Neo_OrderInfo", orderInfo);
            if (payDate != null) params.put("Neo_PayDate", payDate);
            params.put("Neo_ResponseCode", responseCode);
            params.put("Neo_TmnCode", tmnCode);
            params.put("Neo_TransactionNo", transactionNo);
            params.put("Neo_TransactionStatus", transactionStatus);
            params.put("Neo_TxnRef", txnRef);
            
            String expectedHash = generateSecureHash(params);
            
            if (secureHash.equalsIgnoreCase(expectedHash)) {
                log.info("✅ IPN validation successful for txnRef: {}", txnRef);
                
                // Process payment result
                if ("00".equals(responseCode) && "00".equals(transactionStatus)) {
                    log.info("💰 Payment SUCCESS for txnRef: {}, amount: {}", txnRef, amount);
                    // Merchant logic: Update order status to PAID
                } else {
                    log.warn("❌ Payment FAILED for txnRef: {}, responseCode: {}", txnRef, responseCode);
                    // Merchant logic: Update order status to FAILED
                }
                
                // Return success response to Neo Payment Gateway
                return "RspCode=00&Message=OK";
            } else {
                log.error("🚨 IPN validation FAILED for txnRef: {}", txnRef);
                log.error("Expected hash: {}", expectedHash);
                log.error("Received hash: {}", secureHash);
                
                // Return error response
                return "RspCode=97&Message=Invalid signature";
            }
            
        } catch (Exception e) {
            log.error("🔥 Error processing IPN callback for txnRef: {}", txnRef, e);
            return "RspCode=99&Message=System error";
        }
    }
    
    private String generateSecureHash(Map<String, String> params) {
        try {
            // Build hash string
            StringBuilder hashData = new StringBuilder();
            params.entrySet().stream()
                   .sorted(Map.Entry.comparingByKey())
                   .forEach(entry -> {
                       if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                           hashData.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                       }
                   });
            
            // Remove trailing &
            if (hashData.length() > 0) {
                hashData.setLength(hashData.length() - 1);
            }
            
            // Append secret key
            hashData.append("&").append(secretKey);
            
            // Generate SHA-256 hash
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(hashData.toString().getBytes("UTF-8"));
            
            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString().toLowerCase();
            
        } catch (Exception e) {
            log.error("Error generating secure hash", e);
            return "";
        }
    }
}