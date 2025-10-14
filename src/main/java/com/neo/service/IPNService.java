package com.neo.service;

import com.neo.cache.PmPartnerCache;
import com.neo.dto.IPNRequest;
import com.neo.modal.Partner;
import com.neo.util.NeoUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class IPNService {

    private final PmPartnerCache pmPartnerCache;
    private final RestTemplate restTemplate = new RestTemplate();
    private final BlockingQueue<IPNRequest> blockingQueue;

    @PostConstruct
    public void init() {
        Thread workerThread = new Thread(this::processQueue, "TransactionQueueWorker");
        workerThread.setDaemon(true);
        workerThread.start();
        log.info("Started transaction queue worker thread");
    }

    private void processQueue() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                IPNRequest queuedRequest = blockingQueue.take();
                sendIPNNotification(queuedRequest);
            } catch (InterruptedException e) {
                log.warn("Transaction queue worker interrupted");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @Async
    public void sendIPNNotification(IPNRequest ipnRequest) {
        boolean success = false;
        try {
            String ipnUrl = "";
            Partner partner = pmPartnerCache.getPmPartnerByTmnCode(ipnRequest.getNeo_TmnCode());
            if (partner == null) {
                log.info("Partner for TmnCode: {}", ipnRequest.getNeo_TmnCode());
                CompletableFuture.completedFuture(false);
                return;
            }
//            if (partner.getIpnUrl() == null || partner.getIpnUrl().isEmpty()) {
//                log.info("IPN URL not configured, skipping callback for txnRef: {}", ipnRequest.getNeo_TxnRef());
//                CompletableFuture.completedFuture(false);
//                return;
//
//            }
            if (ipnRequest.getDomain().contains("sandbox.neo.vn")) {
                ipnUrl = "http://payment.mobifone.vn/paygw/epm-api/neo/neo-ipn";
            } else {
                ipnUrl = ipnRequest.getDomain() + "/paygw/epm-api/neo/neo-ipn";
            }
            log.info("Sending IPN callback for txnRef: {}", ipnRequest.getNeo_TxnRef());
            // Generate secure hash first
            String hashData = NeoUtils.buildQueryString(ipnRequest.toMap());
            String secureHash = NeoUtils.hmacSHA512(partner.getSecretKey(), hashData);
            hashData += "&Neo_SecureHash=" + secureHash;
            String fullUrl = ipnUrl + "?" + hashData;
            log.info("IPN URL: {}", fullUrl);
            // Send GET request
            ResponseEntity<String> response = restTemplate.getForEntity(fullUrl, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                log.info("IPN callback successful for txnRef: {}, response: {}", ipnRequest.getNeo_TxnRef(), responseBody);
            } else {
                log.warn("IPN callback failed with HTTP status: {} for txnRef: {}", response.getStatusCode(), ipnRequest.getNeo_TxnRef());
            }
            CompletableFuture.completedFuture(success);
        } catch (Exception e) {
            log.error("Error sending IPN request for txnRef: {}", ipnRequest.getNeo_TxnRef(), e);
            CompletableFuture.completedFuture(false);
        }
    }
}