package com.neo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundResponse {
    private String neo_ResponseId;       // Mã hệ thống VNPAY sinh
    private String neo_Command;          // refund
    private String neo_TmnCode;          // mã định danh
    private String neo_TxnRef;           // mã tham chiếu giao dịch
    private Long neo_Amount;             // số tiền hoàn
    private String neo_OrderInfo;        // nội dung yêu cầu hoàn
    private String neo_ResponseCode;     // mã phản hồi API
    private String neo_Message;          // mô tả kết quả
    private String neo_BankCode;         // mã ngân hàng / ví
    private String neo_PayDate;          // ngày hoàn trả yyyyMMddHHmmss
    private Long neo_TransactionNo;      // mã giao dịch tại VNPAY
    private String neo_TransactionType;  // 02/03
    private String neo_TransactionStatus;// trạng thái giao dịch
    private String neo_SecureHash;       // checksum
}
