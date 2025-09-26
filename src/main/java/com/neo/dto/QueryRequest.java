package com.neo.dto;

import lombok.Data;

@Data
public class QueryRequest {
    // Bắt buộc
    private String Neo_RequestId;       // Mã hệ thống merchant tự sinh, duy nhất trong ngày
    private String Neo_Version;         // Phiên bản API (hiện tại 2.1.0)
    private String Neo_Command;         // Mã API ("querydr")
    private String Neo_TmnCode;         // Mã định danh kết nối thanh toán
    private String Neo_TxnRef;          // Mã giao dịch thanh toán đã gửi sang VNPAY
    private String Neo_OrderInfo;       // Mô tả thông tin yêu cầu
    // Tuỳ chọn
    private String Neo_TransactionNo;   // Mã giao dịch tại VNPAY (có thể null)
    private String Neo_TransactionDate; // yyyyMMddHHmmss - thời gian ghi nhận giao dịch tại merchant
    private String Neo_CreateDate;      // yyyyMMddHHmmss - thời gian phát sinh request
    private String Neo_IpAddr;          // IP máy chủ gọi API
    private String Neo_SecureHash;      // Checksum để đảm bảo toàn vẹn dữ liệu
}
