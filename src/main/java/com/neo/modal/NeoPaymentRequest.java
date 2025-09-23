package com.neo.modal;

import lombok.Data;

@Data
public class NeoPaymentRequest {
    private String neoVersion;
    private String neoCommand;
    private String neoTmnCode;
    private Long neoAmount;
    private String neoBankCode;
    private String neoCreateDate;
    private String neoCurrCode;
    private String neoIpAddr;
    private String neoLocale;
    private String neoOrderInfo;
    private String neoOrderType;
    private String neoReturnUrl;
    private String neoExpireDate;
    private String neoTxnRef;
    private String neoSecureHash;
}
