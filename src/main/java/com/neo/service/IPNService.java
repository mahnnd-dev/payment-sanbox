package com.neo.service;

import com.neo.dto.IPNRequest;
import com.neo.util.NeoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
                log.info("Sending IPN callback (attempt {}/{}) for txnRef: {}", attempt, maxRetryAttempts, ipnRequest.getNeo_TxnRef());
                success = sendIPNRequest(ipnRequest, attempt);
                if (!success && attempt < maxRetryAttempts) {
                    // Wait before retry (exponential backoff)
                    Thread.sleep(1000L * attempt);
                }
            } catch (Exception e) {
                log.error("IPN callback attempt {}/{} failed for txnRef: {}", attempt, maxRetryAttempts, ipnRequest.getNeo_TxnRef(), e);
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
            String hashData = NeoUtils.buildQueryString(ipnRequest.toMap());
            String secureHash = NeoUtils.hmacSHA512(secretKey, hashData);
            hashData += "&Neo_SecureHash=" + secureHash;
            String fullUrl = ipnUrl + "?" + hashData;
            log.info("IPN URL: {}", fullUrl);
            // Send GET request
            ResponseEntity<String> response = restTemplate.getForEntity(fullUrl, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                log.info("IPN callback successful (attempt {}) for txnRef: {}, response: {}", attempt, ipnRequest.getNeo_TxnRef(), responseBody);
                return true;
            } else {
                log.warn("IPN callback failed with HTTP status: {} for txnRef: {}", response.getStatusCode(), ipnRequest.getNeo_TxnRef());
                return false;
            }
        } catch (Exception e) {
            log.error("Error sending IPN request for txnRef: {}", ipnRequest.getNeo_TxnRef(), e);
            return false;
        }
    }
}