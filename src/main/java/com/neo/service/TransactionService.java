package com.neo.service;

import com.neo.dto.IPNRequest;
import com.neo.dto.TransactionRequest;
import com.neo.modal.TransactionLog;
import com.neo.repository.TransactionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionLogRepository transactionLogRepository;
    private final IPNService ipnService;

    @Transactional
    public void saveTransaction(TransactionRequest dto) {
        TransactionLog log = new TransactionLog();
        log.setCommand(dto.getCommand());
        log.setRequestId(dto.getRequestId());
        log.setVersion(dto.getVersion());
        log.setTmnCode(dto.getTmnCode());
        log.setTxnRef(dto.getTxnRef());
        log.setOrderInfo(dto.getOrderInfo());
        log.setTransactionDate(dto.getTransactionDate());
        log.setCreateDate(dto.getCreateDate());
        log.setIpAddr(dto.getIpAddr());
        log.setResponseCode(dto.getStatus());
        log.setResponseMessage(dto.getStatusMessage());
        log.setTransactionNo(dto.getTransactionNo());
        log.setPayDate(getPayDate());
        log.setCurrCode(dto.getCurrCode());
        log.setLocale(dto.getLocale());
        log.setOrderType(dto.getOrderType());
        log.setExpireDate(dto.getExpireDate());
        log.setBankName(dto.getBankName());
        log.setCardNumber(dto.getCardNumber());
        log.setCardDate(dto.getCardDate());
        log.setCardHolder(dto.getCardHolder());
//        Loại giao dịch tại hệ thống VNPAY:
//        01: GD thanh toán
//        02: Giao dịch hoàn trả toàn phần
//        03: Giao dịch hoàn trả một phần
        log.setTransactionType("01");
        log.setRefundAmount(dto.getRefundAmount());
        log.setRefundReason(dto.getRefundReason());
        log.setTransactionStatus(dto.getStatus());
        log.setAmount(dto.getAmount());
        log.setBankCode(dto.getBankCode());
        transactionLogRepository.save(log);
        sendIPNCallback(log);
    }

    private void sendIPNCallback(TransactionLog transactionLog) {
        try {
            IPNRequest request = new IPNRequest();
            request.setNeo_TmnCode(transactionLog.getTmnCode());
            request.setNeo_Amount(String.valueOf(transactionLog.getAmount()));
            request.setNeo_BankCode(transactionLog.getBankCode());
            request.setNeo_BankTranNo(generateBankTransactionNo(transactionLog.getBankCode()));
            request.setNeo_CardType("ATM");
            request.setNeo_PayDate(transactionLog.getPayDate());
            request.setNeo_OrderInfo(transactionLog.getOrderInfo());
            request.setNeo_TransactionNo(transactionLog.getTransactionNo());
            request.setNeo_ResponseCode(transactionLog.getResponseCode());
            request.setNeo_TransactionStatus(transactionLog.getTransactionStatus());
            request.setNeo_TxnRef(transactionLog.getTxnRef());

            ipnService.sendIPNNotification(request)
                    .thenAccept(success -> {
                        if (success) {
                            log.info("IPN notification sent successfully for txnRef: {}", transactionLog.getTxnRef());
                        } else {
                            log.warn("IPN notification failed for txnRef: {}", transactionLog.getTxnRef());
                        }
                    })
                    .exceptionally(throwable -> {
                        log.error("Error in IPN notification for txnRef: {}", transactionLog.getTxnRef(), throwable);
                        return null;
                    });

        } catch (Exception e) {
            log.error("Error creating IPN callback for txnRef: {}", transactionLog.getTxnRef(), e);
        }
    }

    private String generateBankTransactionNo(String bankCode) {
        return bankCode + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private String getPayDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
}
