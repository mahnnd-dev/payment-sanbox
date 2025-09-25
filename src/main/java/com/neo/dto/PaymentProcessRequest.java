package com.neo.dto;

import lombok.Data;

@Data
public class PaymentProcessRequest {
    // thông tin thẻ
    private String bankCode;
    private String bankName;
    private String cardNumber;
    private String cardHolder;
    private String cardDate;
    // thông tin đơn hàng
    private String tmnCode;
    private String txnRef;
    private String amount;
    private String amountPay;
    //    NeoPaymentRequest
    private String returnUrl;
    private String orderInfo;

}
