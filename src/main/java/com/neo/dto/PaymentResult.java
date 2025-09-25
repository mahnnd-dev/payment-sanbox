package com.neo.dto;

import lombok.Data;

@Data
public class PaymentResult {
    private boolean success;
    private String transactionId;
    private String bankTranNo;
    private String payDate;
    private String cardType;
}
