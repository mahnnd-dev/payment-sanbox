package com.neo.modal;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NeoPaymentRequest {
    private String orderId;
    private String neoAmount;
    private String totalAmount;
    private String transactionFee;
    private String description;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String returnUrl;
    private String cancelUrl;

    // Có thể thêm các fields khác nếu cần
    public String getFormattedAmount() {
        if (neoAmount != null && !neoAmount.isEmpty()) {
            return neoAmount + " VND";
        }
        return "0 VND";
    }

    public String getFormattedTotalAmount() {
        if (totalAmount != null && !totalAmount.isEmpty()) {
            return totalAmount + " VND";
        }
        return getFormattedAmount(); // Fallback về neoAmount nếu totalAmount null
    }
}