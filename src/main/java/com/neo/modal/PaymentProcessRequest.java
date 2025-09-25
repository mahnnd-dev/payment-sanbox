package com.neo.modal;

import lombok.Data;

@Data
public class PaymentProcessRequest {
    private String bankCode;
    private String bankName;
    private String cardNumber;
    private String cardHolder;
    private String cardDate;
    private String txnRef;
    private String orderId;
    private String orderInfor;
    private String amount;
    private String returnUrl;
    private String orderType;
    private String tmnCode;
}
