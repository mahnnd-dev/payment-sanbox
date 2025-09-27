package com.neo.constant;

public enum StatusPaymentConstants {

    SUCCESS("00", "Thanh toán thành công"),
    REFUND("01", "Hoàn giao dịch"),
    CANCELL("02", "Hủy giao dịch"),
    ERROR("04", "Lỗi hệ thống"),
    QUERY("05", "T");

    private final String status;
    private final String message;

    StatusPaymentConstants(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public static String getMessageByStatus(String status) {
        for (StatusPaymentConstants e : values()) {
            if (e.status.equals(status)) return e.message;
        }
        return "Unknown error status.";
    }
}
