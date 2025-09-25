package com.neo.service;

import com.neo.modal.IPNRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class IPNService {

    @Value("${neo.payment.secret-key}")
    private String secretKey;

    @Value("${neo.payment.ipn-url:}")
    private String ipnUrl;

    @Value("${neo.payment.max-retry-attempts:3}")
    private int maxRetryAttempts;

    private final RestTemplate restTemplate = new RestTemplate();

    @Async
    public CompletableFuture<Boolean> sendIPNNotification(IPNRequest ipnRequest) {
        if (ipnUrl == null || ipnUrl.isEmpty()) {
            log.info("IPN URL not configured, skipping callback for txnRef: {}", ipnRequest.getNeo_TxnRef());
            return CompletableFuture.completedFuture(false);
        }

        int attempt = 0;
        boolean success = false;

        while (attempt < maxRetryAttempts && !success) {
            attempt++;
            try {
                log.info("Sending IPN callback (attempt {}/{}) for txnRef: {}",
                        attempt, maxRetryAttempts, ipnRequest.getNeo_TxnRef());

                success = sendIPNRequest(ipnRequest, attempt);

                if (!success && attempt < maxRetryAttempts) {
                    // Wait before retry (exponential backoff)
                    Thread.sleep(1000 * attempt);
                }

            } catch (Exception e) {
                log.error("IPN callback attempt {}/{} failed for txnRef: {}",
                        attempt, maxRetryAttempts, ipnRequest.getNeo_TxnRef(), e);

                if (attempt == maxRetryAttempts) {
                    log.error("All IPN callback attempts failed for txnRef: {}", ipnRequest.getNeo_TxnRef());
                }
            }
        }

        return CompletableFuture.completedFuture(success);
    }

    private boolean sendIPNRequest(IPNRequest ipnRequest, int attempt) {
        try {
            log.info("Sending IPN request (attempt {}) for txnRef: {}", attempt, ipnRequest.getNeo_TxnRef());

            // Generate secure hash first
            String secureHash = generateSecureHash(ipnRequest);

            // Build URL with query parameters using UriComponentsBuilder
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(ipnUrl)
                    .queryParam("Neo_TmnCode", ipnRequest.getNeo_TmnCode())
                    .queryParam("Neo_Amount", ipnRequest.getNeo_Amount())
                    .queryParam("Neo_BankCode", ipnRequest.getNeo_BankCode())
                    .queryParam("Neo_ResponseCode", ipnRequest.getNeo_ResponseCode())
                    .queryParam("Neo_TransactionStatus", ipnRequest.getNeo_TransactionStatus())
                    .queryParam("Neo_TxnRef", ipnRequest.getNeo_TxnRef())
                    .queryParam("Neo_TransactionNo", ipnRequest.getNeo_TransactionNo())
                    .queryParam("Neo_OrderInfo", ipnRequest.getNeo_OrderInfo());

            // Add optional parameters if they exist
            if (ipnRequest.getNeo_BankTranNo() != null && !ipnRequest.getNeo_BankTranNo().isEmpty()) {
                builder.queryParam("Neo_BankTranNo", ipnRequest.getNeo_BankTranNo());
            }
            if (ipnRequest.getNeo_CardType() != null && !ipnRequest.getNeo_CardType().isEmpty()) {
                builder.queryParam("Neo_CardType", ipnRequest.getNeo_CardType());
            }
            if (ipnRequest.getNeo_PayDate() != null && !ipnRequest.getNeo_PayDate().isEmpty()) {
                builder.queryParam("Neo_PayDate", ipnRequest.getNeo_PayDate());
            }

            // Add secure hash last
            builder.queryParam("Neo_SecureHash", secureHash);

            String fullUrl = builder.toUriString();
            log.info("IPN URL: {}", fullUrl);

            // Send GET request
            ResponseEntity<String> response = restTemplate.getForEntity(fullUrl, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                log.info("IPN callback successful (attempt {}) for txnRef: {}, response: {}",
                        attempt, ipnRequest.getNeo_TxnRef(), responseBody);

                // Check if merchant returns "RspCode=00" (standard success response)
                if (responseBody != null && responseBody.contains("RspCode=00")) {
                    return true;
                } else {
                    log.warn("IPN callback returned non-success response for txnRef: {}, response: {}",
                            ipnRequest.getNeo_TxnRef(), responseBody);
                    // Still consider it successful if HTTP status is OK (some merchants don't return RspCode)
                    return true;
                }
            } else {
                log.warn("IPN callback failed with HTTP status: {} for txnRef: {}",
                        response.getStatusCode(), ipnRequest.getNeo_TxnRef());
                return false;
            }

        } catch (Exception e) {
            log.error("Error sending IPN request for txnRef: {}", ipnRequest.getNeo_TxnRef(), e);
            return false;
        }
    }

    private String generateSecureHash(IPNRequest ipnRequest) {
        try {
            // Create sorted parameters map
            Map<String, String> params = new LinkedHashMap<>();
            params.put("Neo_Amount", ipnRequest.getNeo_Amount());
            params.put("Neo_BankCode", ipnRequest.getNeo_BankCode());
            if (ipnRequest.getNeo_BankTranNo() != null) params.put("Neo_BankTranNo", ipnRequest.getNeo_BankTranNo());
            if (ipnRequest.getNeo_CardType() != null) params.put("Neo_CardType", ipnRequest.getNeo_CardType());
            params.put("Neo_OrderInfo", ipnRequest.getNeo_OrderInfo());
            if (ipnRequest.getNeo_PayDate() != null) params.put("Neo_PayDate", ipnRequest.getNeo_PayDate());
            params.put("Neo_ResponseCode", ipnRequest.getNeo_ResponseCode());
            params.put("Neo_TmnCode", ipnRequest.getNeo_TmnCode());
            params.put("Neo_TransactionNo", ipnRequest.getNeo_TransactionNo());
            params.put("Neo_TransactionStatus", ipnRequest.getNeo_TransactionStatus());
            params.put("Neo_TxnRef", ipnRequest.getNeo_TxnRef());

            // Build hash string - sort by key
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

            log.debug("Hash data string: {}", hashData.toString());

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
            log.error("Error generating secure hash for txnRef: {}", ipnRequest.getNeo_TxnRef(), e);
            return "";
        }
    }
}