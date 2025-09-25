package com.neo.controller;

import com.neo.modal.IPNRequest;
import com.neo.modal.PaymentProcessRequest;
import com.neo.modal.PaymentResult;
import com.neo.service.impl.IPNService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/neo-payment")
@RequiredArgsConstructor
public class PaymentApiController {

    @Value("${neo.payment.secret-key:your-secret-key}")
    private String secretKey;

    private final IPNService ipnService;

    @PostMapping("/process")
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

                // Tạo redirect URL về merchant
                String redirectUrl = buildRedirectUrl(request, paymentResult, "00");
                response.put("redirectUrl", redirectUrl);

                // Gọi IPN callback async (không chặn response về frontend)
                sendIPNCallback(request, paymentResult, "00", "00");

                log.info("Payment processed successfully for txnRef: {}", request.getTxnRef());
            } else {
                // Failure response
                response.put("success", false);
                response.put("message", "Giao dịch bị từ chối bởi ngân hàng");
                response.put("errorCode", "BANK_DECLINED");

                // Vẫn tạo redirect URL cho thất bại
                String redirectUrl = buildRedirectUrl(request, paymentResult, "99");
                response.put("redirectUrl", redirectUrl);

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

    private String buildRedirectUrl(PaymentProcessRequest request, PaymentResult paymentResult, String responseCode) {
        if (request.getReturnUrl() == null || request.getReturnUrl().isEmpty()) {
            return "/payment/result?status=" + (responseCode.equals("00") ? "SUCCESS" : "FAILED");
        }

        // Build return URL với các tham số Neo Payment
        StringBuilder url = new StringBuilder(request.getReturnUrl());
        url.append(request.getReturnUrl().contains("?") ? "&" : "?");

        Map<String, String> params = new LinkedHashMap<>();
        params.put("Neo_TmnCode", request.getTmnCode());
        params.put("Neo_Amount", request.getAmount());
        params.put("Neo_BankCode", request.getBankCode());
        params.put("Neo_BankTranNo", paymentResult.getBankTranNo());
        params.put("Neo_CardType", paymentResult.getCardType());
        params.put("Neo_PayDate", paymentResult.getPayDate());
        params.put("Neo_OrderInfo", request.getOrderId()); // Simplified
        params.put("Neo_TransactionNo", paymentResult.getTransactionId());
        params.put("Neo_ResponseCode", responseCode);
        params.put("Neo_TransactionStatus", responseCode);
        params.put("Neo_TxnRef", request.getTxnRef());

        // Generate secure hash
        String secureHash = generateSecureHash(params);
        params.put("Neo_SecureHash", secureHash);

        // Append parameters to URL
        params.forEach((key, value) -> {
            url.append(key).append("=").append(value).append("&");
        });

        // Remove trailing &
        if (url.charAt(url.length() - 1) == '&') {
            url.setLength(url.length() - 1);
        }

        return url.toString();
    }

    private void sendIPNCallback(PaymentProcessRequest request, PaymentResult paymentResult,
                                 String responseCode, String transactionStatus) {
        try {
            // Create IPN request object
            IPNRequest ipnRequest = new IPNRequest(
                    request.getTmnCode(),
                    request.getAmount(),
                    request.getBankCode(),
                    paymentResult.getBankTranNo(),
                    paymentResult.getCardType(),
                    paymentResult.getPayDate(),
                    request.getOrderInfor(),
                    paymentResult.getTransactionId(),
                    responseCode,
                    transactionStatus,
                    request.getTxnRef()
            );
            log.info("---------------------{}", ipnRequest.toString());
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

    private String generateSecureHash(Map<String, String> params) {
        try {
            // Sort parameters by key
            StringBuilder data = new StringBuilder();
            params.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        if (!"Neo_SecureHash".equals(entry.getKey())) {
                            data.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                        }
                    });

            // Remove trailing &
            if (data.length() > 0) {
                data.setLength(data.length() - 1);
            }

            // Append secret key
            data.append("&").append(secretKey);

            // Generate SHA-256 hash
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(data.toString().getBytes("UTF-8"));

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