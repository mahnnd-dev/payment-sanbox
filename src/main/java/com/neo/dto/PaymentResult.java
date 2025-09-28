package com.neo.dto;

import lombok.Data;

@Data
public class PaymentResult {
    private String status;
    private String message;
    private String txnRef;
    private long timestamp = System.currentTimeMillis();
}
