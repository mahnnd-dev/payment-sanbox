package com.neo.modal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NeoPaymentRequest {
    // Neo Payment Parameters (tương tự VNPAY)
    private String Neo_Amount;
    private String Neo_Command;
    private String Neo_CreateDate;
    private String Neo_CurrCode;
    private String Neo_ExpireDate;
    private String Neo_IpAddr;
    private String Neo_Locale;
    private String Neo_OrderInfo;
    private String Neo_OrderType;
    private String Neo_ReturnUrl;
    private String Neo_TmnCode;
    private String Neo_TxnRef;
    private String Neo_Version;
    private String Neo_SecureHash;

    // Helper methods để format dữ liệu
    public String getFormattedAmount() {
        if (Neo_Amount != null && !Neo_Amount.isEmpty()) {
            try {
                long amount = Long.parseLong(Neo_Amount);
                // Sử dụng DecimalFormat để thêm dấu phân cách hàng nghìn
                DecimalFormat formatter = new DecimalFormat("#,###");
                return formatter.format(amount) + " " + (Neo_CurrCode != null ? Neo_CurrCode : "");
            } catch (NumberFormatException e) {
                return Neo_Amount + " " + (Neo_CurrCode != null ? Neo_CurrCode : "");
            }
        }
        return "0";
    }

    public String getOrderId() {
        return Neo_TxnRef != null ? Neo_TxnRef : "Unknown";
    }

    public String getFormattedCreateDate() {
        if (Neo_CreateDate != null && Neo_CreateDate.length() == 14) {
            try {
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                LocalDateTime dateTime = LocalDateTime.parse(Neo_CreateDate, inputFormatter);
                return dateTime.format(outputFormatter);
            } catch (Exception e) {
                return Neo_CreateDate;
            }
        }
        return Neo_CreateDate;
    }

    public String getFormattedExpireDate() {
        if (Neo_ExpireDate != null && Neo_ExpireDate.length() == 14) {
            try {
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                LocalDateTime dateTime = LocalDateTime.parse(Neo_ExpireDate, inputFormatter);
                return dateTime.format(outputFormatter);
            } catch (Exception e) {
                return Neo_ExpireDate;
            }
        }
        return Neo_ExpireDate;
    }

    public String getDecodedOrderInfo() {
        if (Neo_OrderInfo != null) {
            try {
                return URLDecoder.decode(Neo_OrderInfo, StandardCharsets.UTF_8);
            } catch (Exception e) {
                return Neo_OrderInfo;
            }
        }
        return "";
    }

    public String getSupplierName() {
        // Có thể parse từ Neo_TmnCode hoặc từ OrderInfo
        if (Neo_TmnCode != null) {
            return "Merchant " + Neo_TmnCode;
        }
        return "Neo Payment";
    }

    // Compatibility methods để không phá vỡ template hiện tại
    public String getNeoAmount() {
        return getFormattedAmount();
    }

    public String getTotalAmount() {
        return getFormattedAmount();
    }
}