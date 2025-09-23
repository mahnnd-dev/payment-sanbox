package com.neo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class PaymentNeoController {

//    @Value("${vnpay.hash-secret}")
    private String vnpayHashSecret = "snjfjsfhjsdfds";

    /**
     * Endpoint nhận callback/return từ VNPay.
     * <p>
     * Ví dụ: vnp_ReturnUrl=http://localhost:9001/api/vnpay/call-back
     */
    @GetMapping("/api/vnpay/call-back")
    public ResponseEntity<Map<String, Object>> vnpayReturn(@RequestParam Map<String, String> allParams) {
        Map<String, Object> resp = new HashMap<>();

        // 1. Lấy ra secure hash client gửi, và bỏ 2 param secure hash nếu có
        String vnpSecureHash = allParams.get("vnp_SecureHash");
        if (vnpSecureHash == null) {
            resp.put("ok", false);
            resp.put("message", "Missing vnp_SecureHash");
            return ResponseEntity.badRequest().body(resp);
        }

        // 2. Tạo map mới chứa các param cần dùng để build hash (loại bỏ vnp_SecureHash & vnp_SecureHashType)
        Map<String, String> fields = new HashMap<>();
        for (Map.Entry<String, String> e : allParams.entrySet()) {
            String k = e.getKey();
            String v = e.getValue();
            if (k == null) continue;
            if ("vnp_SecureHash".equals(k) || "vnp_SecureHashType".equals(k)) continue;
            fields.put(k, v);
        }

        // 3. Sắp xếp key theo thứ tự từ điển (lexicographical) và build chuỗi hashData: key=value&key2=value2...
        List<String> fieldKeys = new ArrayList<>(fields.keySet());
        Collections.sort(fieldKeys);
        StringBuilder hashData = new StringBuilder();
        for (int i = 0; i < fieldKeys.size(); i++) {
            String key = fieldKeys.get(i);
            String value = fields.get(key);
            // VNPay dùng dạng key=value (value là giá trị decode). Không URL-encode trong chuỗi để hash.
            if (value != null && value.length() > 0) {
                if (hashData.length() > 0) {
                    hashData.append('&');
                }
                hashData.append(key).append('=').append(value);
            }
        }

        // 4. Tính HMAC SHA512 của hashData bằng vnpayHashSecret
        String computedHash = hmacSHA512(vnpayHashSecret, hashData.toString());

        // 5. So sánh (chú ý: VNPay trả hash thường là hex lowercase)
        boolean valid = computedHash != null && computedHash.equalsIgnoreCase(vnpSecureHash);

        // 6. Lấy các param cần thiết để xử lý nghiệp vụ
        String txnRef = allParams.get("vnp_TxnRef"); // mã tham chiếu của bạn
        String orderInfo = allParams.get("vnp_OrderInfo");
        String responseCode = allParams.get("vnp_ResponseCode"); // "00" = success
        String amount = allParams.get("vnp_Amount"); // thường tính bằng VND * 100 (xem doc VNPay)
        String bankCode = allParams.get("vnp_BankCode");
        String payDate = allParams.get("vnp_PayDate");

        // 7. Business logic: verify && responseCode == "00" => mark order as paid
        if (valid) {
            if ("00".equals(responseCode)) {
                // TODO: cập nhật trạng thái đơn hàng trong DB (tìm theo txnRef / orderId)
                // orderService.markPaid(txnRef, amount, bankCode, payDate, allParams);
                resp.put("status", "success");
                resp.put("message", "Payment successful and signature valid");
            } else {
                // thanh toán thất bại theo response code
                // TODO: cập nhật trạng thái thất bại
                resp.put("status", "failed");
                resp.put("message", "Payment failed. vnp_ResponseCode=" + responseCode);
            }
        } else {
            // signature không hợp lệ -> khả năng tấn công hoặc dữ liệu bị sửa
            resp.put("status", "invalid_signature");
            resp.put("message", "Invalid signature");
        }

        // Trả về JSON cho test (hoặc bạn có thể redirect người dùng về trang kết quả)
        resp.put("vnp_SecureHash_received", vnpSecureHash);
        resp.put("vnp_SecureHash_computed", computedHash);
        resp.put("vnp_TxnRef", txnRef);
        resp.put("vnp_OrderInfo", orderInfo);
        resp.put("vnp_ResponseCode", responseCode);
        return ResponseEntity.ok(resp);
    }

    /**
     * Compute HMAC SHA512 and return hex string (lowercase).
     */
    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] hashBytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (Exception e) {
            // log error
            e.printStackTrace();
            return null;
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}

