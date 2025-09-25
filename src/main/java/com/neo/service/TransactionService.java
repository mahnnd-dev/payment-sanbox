package com.neo.service;

import com.neo.dto.IPNRequest;
import com.neo.dto.PaymentResult;
import com.neo.dto.TransactionRequest;
import com.neo.modal.QueryTransactionEntity;
import com.neo.modal.RefundTransactionEntity;
import com.neo.repository.QueryTransactionRepository;
import com.neo.repository.RefundTransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class TransactionService {

    private final QueryTransactionRepository queryRepo;
    private final RefundTransactionRepository refundRepo;
    private final IPNService ipnService;

    public TransactionService(QueryTransactionRepository queryRepo,
                              RefundTransactionRepository refundRepo, IPNService ipnService) {
        this.queryRepo = queryRepo;
        this.refundRepo = refundRepo;
        this.ipnService = ipnService;
    }

    @Transactional
    public void saveTransaction(TransactionRequest dto) {
        log.info("TransactionService: {}", dto);
        String responseCode = "";
        String responseMessage = "";
        PaymentResult paymentResult = processNeoPayment(dto);
        if ("SUCCESS".equalsIgnoreCase(dto.getStatus())) {
            responseCode = "00";
            responseMessage = "Thanh toán thành công";
            QueryTransactionEntity entity = new QueryTransactionEntity();
            entity.setRequestId(dto.getRequestId());
            entity.setVersion(dto.getVersion());
            entity.setCommand(dto.getCommand());
            entity.setTmnCode(dto.getTmnCode());
            entity.setTxnRef(dto.getTxnRef());
            entity.setOrderInfo(dto.getOrderInfo());
            entity.setTransactionNo(dto.getTransactionNo());
            entity.setTransactionDate(dto.getTransactionDate());
            entity.setCreateDate(dto.getCreateDate());
            entity.setIpAddr(dto.getIpAddr());
            entity.setResponseCode(responseCode);
            entity.setResponseMessage(responseMessage);
            entity.setStatus("SUCCESS");
            entity.setCreatedAt(LocalDateTime.now());
            queryRepo.save(entity);
        } else {
            responseCode = "99";
            responseMessage = "Thanh toán thất bại";
            RefundTransactionEntity entity = new RefundTransactionEntity();
            entity.setRequestId(dto.getRequestId());
            entity.setVersion(dto.getVersion());
            entity.setCommand(dto.getCommand());
            entity.setTmnCode(dto.getTmnCode());
            entity.setTxnRef(dto.getTxnRef());
            entity.setOrderInfo(dto.getOrderInfo());
            entity.setTransactionNo(dto.getTransactionNo());
            entity.setTransactionDate(dto.getTransactionDate());
            entity.setCreateDate(dto.getCreateDate());
            entity.setIpAddr(dto.getIpAddr());
            entity.setAmount(dto.getAmount());
            entity.setRefundAmount(dto.getAmount());
            entity.setRefundReason("Thanh toán thất bại");
            entity.setResponseCode(responseCode);
            entity.setResponseMessage(responseMessage);
            entity.setStatus("FAILED");
            entity.setCreatedAt(LocalDateTime.now());
            refundRepo.save(entity);
        }
        sendIPNCallback(dto, paymentResult, responseCode, responseMessage);
    }

    private void sendIPNCallback(TransactionRequest request, PaymentResult paymentResult, String responseCode, String transactionStatus) {
        try {
            // Create IPN request object
            IPNRequest ipnRequest = new IPNRequest(
                    request.getTmnCode(),
                    String.valueOf(request.getAmount()),
                    request.getBankCode(),
                    paymentResult.getBankTranNo(),
                    paymentResult.getCardType(),
                    paymentResult.getPayDate(),
                    request.getOrderInfo(),
                    paymentResult.getTransactionId(),
                    responseCode,
                    transactionStatus,
                    request.getTxnRef());
            // Send IPN notification asynchronously
            ipnService.sendIPNNotification(ipnRequest)
                    .thenAccept(success -> {
                        if (success) {
                            log.info("IPN notification sent successfully for txnRef: {}", request.getTxnRef());
                        } else {
                            log.warn("IPN notification failed for txnRef: {}", request.getTxnRef());
                        }
                    })
                    .exceptionally(throwable -> {
                        log.error("Error in IPN notification for txnRef: {}", request.getTxnRef(), throwable);
                        return null;
                    });

        } catch (Exception e) {
            log.error("Error creating IPN callback for txnRef: {}", request.getTxnRef(), e);
        }
    }

    private PaymentResult processNeoPayment(TransactionRequest request) {
        PaymentResult result = new PaymentResult();

        try {
            // Simulate processing time
            Thread.sleep(1500);
            // Generate transaction details
            String transactionId = generateTransactionId();
            String bankTranNo = generateBankTransactionNo(request.getBankCode());
            String payDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            // Simulate success rate (90% success)
            boolean success = Math.random() > 0.1;
            result.setSuccess(success);
            result.setTransactionId(transactionId);
            result.setBankTranNo(bankTranNo);
            result.setPayDate(payDate);
            result.setCardType("ATM"); // Default card type
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            result.setSuccess(false);
        }
        return result;
    }

    private String generateTransactionId() {
        return String.valueOf(System.currentTimeMillis());
    }

    private String generateBankTransactionNo(String bankCode) {
        return bankCode + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
}
