package com.neo.service;

import com.neo.dto.IPNRequest;
import com.neo.util.EnCodeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
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
                    Thread.sleep(1000L * attempt);
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
            String fullUrl = generateSecureHash(ipnRequest);
            log.info("IPN URL: {}", fullUrl);
            // Send GET request
            ResponseEntity<String> response = restTemplate.getForEntity(fullUrl, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                log.info("IPN callback successful (attempt {}) for txnRef: {}, response: {}", attempt, ipnRequest.getNeo_TxnRef(), responseBody);
                // Check if merchant returns "RspCode=00" (standard success response)
                if (responseBody.contains("RspCode=00")) {
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
            Map<String, String> params = new HashMap<>();
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
            return EnCodeUtils.buildUrl(ipnUrl, secretKey, params);
        } catch (Exception e) {
            log.error("Error generating secure hash for txnRef: {}", ipnRequest.getNeo_TxnRef(), e);
            return "";
        }
    }
}