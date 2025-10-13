package com.neo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NeoPaymentRequest {
    private String Neo_Version;
    private String Neo_Command;
    private String Neo_TmnCode;
    private String Neo_Amount;
    private String Neo_BankCode;       // Tùy chọn
    private String Neo_CreateDate;
    private String Neo_CurrCode;
    private String Neo_IpAddr;
    private String Neo_Locale;
    private String Neo_OrderInfo;
    private String Neo_OrderType;
    private String Neo_ReturnUrl;
    private String Neo_ExpireDate;
    private String Neo_TxnRef;
    private String domain;
    private String Neo_SecureHash;
}
