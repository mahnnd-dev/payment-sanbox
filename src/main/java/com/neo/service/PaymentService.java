package com.neo.service;

import com.neo.dto.IPNRequest;
import com.neo.dto.PaymentProcessRequest;
import com.neo.dto.PaymentResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    @Value("${neo.payment.secret-key:your-secret-key}")
    private String secretKey;

    private final IPNService ipnService;

    public ResponseEntity<Map<String, Object>> processPayment(@RequestBody PaymentProcessRequest request) {
        log.info("Processing payment request: {}", request);

        Map<String, Object> response = new HashMap<>();

        try {
            // Validate request
            if (!isValidPaymentRequest(request)) {
                response.put("success", false);
                response.put("message", "Thông tin thanh toán không hợp lệ");
                return ResponseEntity.badRequest().body(response);
            }

            // Simulate payment processing
            PaymentResult paymentResult = processNeoPayment(request);

            if (paymentResult.isSuccess()) {
                // Success response
                response.put("success", true);
                response.put("message", "Thanh toán thành công");
                response.put("transactionId", paymentResult.getTransactionId());
                response.put("bankTranNo", paymentResult.getBankTranNo());
                response.put("amount", request.getAmount());
                response.put("bankName", request.getBankName());
                // Gọi IPN callback async (không chặn response về frontend)
                sendIPNCallback(request, paymentResult, "00", "00");
                log.info("Payment processed successfully for txnRef: {}", request.getTxnRef());
            } else {
                // Failure response
                response.put("success", false);
                response.put("message", "Giao dịch bị từ chối bởi ngân hàng");
                response.put("errorCode", "BANK_DECLINED");
                // Gọi IPN callback cho thất bại
                sendIPNCallback(request, paymentResult, "99", "99");
                log.warn("Payment failed for txnRef: {}", request.getTxnRef());
            }

        } catch (Exception e) {
            log.error("Error processing payment: ", e);
            response.put("success", false);
            response.put("message", "Lỗi hệ thống, vui lòng thử lại sau");
            return ResponseEntity.internalServerError().body(response);
        }

        return ResponseEntity.ok(response);
    }

    private boolean isValidPaymentRequest(PaymentProcessRequest request) {
        return request.getTxnRef() != null && !request.getTxnRef().isEmpty() &&
                request.getAmount() != null && !request.getAmount().isEmpty() &&
                request.getCardNumber() != null && !request.getCardNumber().isEmpty() &&
                request.getCardHolder() != null && !request.getCardHolder().isEmpty() &&
                request.getBankCode() != null && !request.getBankCode().isEmpty();
    }

    private PaymentResult processNeoPayment(PaymentProcessRequest request) {
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

    private void sendIPNCallback(PaymentProcessRequest request, PaymentResult paymentResult, String responseCode, String transactionStatus) {
        try {
            // Create IPN request object
            IPNRequest ipnRequest = new IPNRequest(
                    request.getTmnCode(),
                    request.getAmount(),
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
}
