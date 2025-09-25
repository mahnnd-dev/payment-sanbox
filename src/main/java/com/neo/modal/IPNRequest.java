package com.neo.modal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IPNRequest {
    private String neo_TmnCode;
    private String neo_Amount;
    private String neo_BankCode;
    private String neo_BankTranNo;
    private String neo_CardType;
    private String neo_PayDate;
    private String neo_OrderInfo;
    private String neo_TransactionNo;
    private String neo_ResponseCode;
    private String neo_TransactionStatus;
    private String neo_TxnRef;
}