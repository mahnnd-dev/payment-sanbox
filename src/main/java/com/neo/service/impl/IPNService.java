package com.neo.service.impl;

import com.neo.modal.IPNRequest;
import com.neo.util.EnCodeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
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
            // Prepare query parameters
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ipnUrl)
                    .queryParam("Neo_TmnCode", ipnRequest.getNeo_TmnCode())
                    .queryParam("Neo_Amount", ipnRequest.getNeo_Amount())
                    .queryParam("Neo_BankCode", ipnRequest.getNeo_BankCode())
                    .queryParam("Neo_BankTranNo", ipnRequest.getNeo_BankTranNo())
                    .queryParam("Neo_CardType", ipnRequest.getNeo_CardType())
                    .queryParam("Neo_PayDate", ipnRequest.getNeo_PayDate())
                    .queryParam("Neo_OrderInfo", ipnRequest.getNeo_OrderInfo())
                    .queryParam("Neo_TransactionNo", ipnRequest.getNeo_TransactionNo())
                    .queryParam("Neo_ResponseCode", ipnRequest.getNeo_ResponseCode())
                    .queryParam("Neo_TransactionStatus", ipnRequest.getNeo_TransactionStatus())
                    .queryParam("Neo_TxnRef", ipnRequest.getNeo_TxnRef())
                    .queryParam("Neo_SecureHash", generateSecureHash(ipnRequest));

            String fullUrl = builder.toUriString();

            // Send GET request
            ResponseEntity<String> response = restTemplate.getForEntity(fullUrl, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                log.info("IPN callback successful (attempt {}) for txnRef: {}, response: {}",
                        attempt, ipnRequest.getNeo_TxnRef(), responseBody);

                if (responseBody.contains("RspCode=00")) {
                    return true;
                } else {
                    log.warn("IPN callback returned non-success response for txnRef: {}, response: {}",
                            ipnRequest.getNeo_TxnRef(), responseBody);
                    return false;
                }
            } else {
                log.warn("IPN callback failed with HTTP status: {} for txnRef: {}",
                        response.getStatusCode(), ipnRequest.getNeo_TxnRef());
                return false;
            }

        } catch (Exception e) {
            log.error("Error sending IPN GET request for txnRef: {}", ipnRequest.getNeo_TxnRef(), e);
            return false;
        }
    }


    private String generateSecureHash(IPNRequest ipnRequest) {
        try {
            // Create sorted parameters map
            Map<String, String> params = new HashMap<>();
            params.put("Neo_TmnCode", ipnRequest.getNeo_TmnCode());
            params.put("Neo_Amount", ipnRequest.getNeo_Amount());
            params.put("Neo_BankCode", ipnRequest.getNeo_BankCode());
            params.put("Neo_BankTranNo", ipnRequest.getNeo_BankTranNo());
            params.put("Neo_CardType", ipnRequest.getNeo_CardType());
            params.put("Neo_PayDate", ipnRequest.getNeo_PayDate());
            params.put("Neo_OrderInfo", ipnRequest.getNeo_OrderInfo());
            params.put("Neo_TransactionNo", ipnRequest.getNeo_TransactionNo());
            params.put("Neo_ResponseCode", ipnRequest.getNeo_ResponseCode());
            params.put("Neo_TransactionStatus", ipnRequest.getNeo_TransactionStatus());
            params.put("Neo_TxnRef", ipnRequest.getNeo_TxnRef());

            List<String> fieldNames = new ArrayList<>(params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    //Build hash data
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
            String queryUrl = query.toString();
            String vnp_SecureHash = EnCodeUtils.hmacSHA512(secretKey, hashData.toString());
            queryUrl += "&Neo_SecureHash=" + vnp_SecureHash;
            return queryUrl;
        } catch (Exception e) {
            log.error("Error generating secure hash for txnRef: {}", ipnRequest.getNeo_TxnRef(), e);
            return "";
        }
    }
}