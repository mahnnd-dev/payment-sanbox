package com.neo.service;

import com.neo.dto.IPNRequest;
import com.neo.dto.TransactionRequest;
import com.neo.modal.TransactionLog;
import com.neo.repository.TransactionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionLogRepository transactionLogRepository;
    private final IPNService ipnService;

    @Transactional
    public void saveTransaction(TransactionRequest dto) {
        TransactionLog log = new TransactionLog();
        log.setCommand(dto.getCommand());
        log.setRequestId(dto.getRequestId());
        log.setVersion(dto.getVersion());
        log.setTmnCode(dto.getTmnCode());
        log.setTxnRef(dto.getTxnRef());
        log.setOrderInfo(dto.getOrderInfo());
        log.setTransactionDate(dto.getTransactionDate());
        log.setCreateDate(dto.getCreateDate());
        log.setIpAddr(dto.getIpAddr());
//        log.setRequestSecureHash(dto.getR());
        log.setResponseCode(dto.getStatus());
        log.setResponseMessage(dto.getStatusMessage());
        log.setResponseTransactionNo(dto.getTransactionNo());
        log.setResponsePayDate(dto.getCardDate());
//        log.setResponseSecureHash(dto.getCardDate());
        log.setTransactionType("NEOPAY");
        log.setRefundAmount(dto.getRefundAmount());
//        log.setCreateBy(dto.getC());
        log.setResponseTransactionStatus(dto.getStatus());
        log.setResponseAmount(dto.getAmount());
        log.setResponseBankCode(dto.getBankCode());
//        log.setCreatedAt(dto.getC());
//        log.setUpdatedAt(dto.());
        transactionLogRepository.save(log);
//        sendIPNCallback(log);
    }

    private void sendIPNCallback(TransactionLog transactionLog) {
        try {
            // Create IPN request object
            IPNRequest ipnRequest = new IPNRequest(
                    transactionLog.getTmnCode(),
                    String.valueOf(transactionLog.getResponseAmount()),
                    generateBankTransactionNo(transactionLog.getResponseBankCode()),
                    transactionLog.getResponseTransactionNo(),
                    transactionLog.getTransactionType(),
                    transactionLog.getResponsePayDate(),
                    transactionLog.getOrderInfo(),
                    generateTransactionId(),
                    transactionLog.getResponseCode(),
                    transactionLog.getResponseMessage(),
                    transactionLog.getTxnRef());
            // Send IPN notification asynchronously
            ipnService.sendIPNNotification(ipnRequest)
                    .thenAccept(success -> {
                        if (success) {
                            log.info("IPN notification sent successfully for txnRef: {}", transactionLog.getTxnRef());
                        } else {
                            log.warn("IPN notification failed for txnRef: {}", transactionLog.getTxnRef());
                        }
                    })
                    .exceptionally(throwable -> {
                        log.error("Error in IPN notification for txnRef: {}", transactionLog.getTxnRef(), throwable);
                        return null;
                    });

        } catch (Exception e) {
            log.error("Error creating IPN callback for txnRef: {}", transactionLog.getTxnRef(), e);
        }
    }

    private String generateTransactionId() {
        return String.valueOf(System.currentTimeMillis());
    }

    private String generateBankTransactionNo(String bankCode) {
        return bankCode + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
}
