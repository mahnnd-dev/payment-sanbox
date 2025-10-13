package com.neo.service;

import com.neo.cache.PmBankerCache;
import com.neo.cache.PmPartnerCache;
import com.neo.dto.IPNRequest;
import com.neo.dto.TransactionRequest;
import com.neo.modal.Banker;
import com.neo.modal.Partner;
import com.neo.modal.TransactionLog;
import com.neo.repository.TransactionLogRepository;
import com.neo.util.NeoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionLogRepository transactionLogRepository;
    private final PmPartnerCache pmPartnerCache;
    private final PmBankerCache pmBankerCache;
    private final BlockingQueue<IPNRequest> blockingQueue;

    public Map<String, Serializable> validateCard(Banker banker) {
        boolean valid = true;
        String message = "";
        if (pmBankerCache.getPmBankerByCardName(banker.getCardNumber()) == null) {
            valid = false;
            message = "Mã thẻ không hợp lệ";
            return Map.of("valid", valid, "message", message);
        }
        Banker b = pmBankerCache.getPmBankerByCardName(banker.getCardNumber());
        if (b != null && !b.getCardHolder().equals(banker.getCardHolder())) {
            valid = false;
            message = "Tên chủ thẻ không hợp lệ";
            return Map.of("valid", valid, "message", message);
        }
        return Map.of("valid", valid, "message", "OK");
    }

    @Transactional
    public String saveTransaction(TransactionRequest dto) {
        log.info("TransactionService saveTransaction: {}", dto);
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
        log.setReturnUrl(dto.getReturnUrl());
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
        log.setDomain(dto.getDomain());
        transactionLogRepository.save(log);
        return sendIPNCallback(log);
    }

    private String sendIPNCallback(TransactionLog transactionLog) {
        try {
            Partner partner = pmPartnerCache.getPmPartnerByTmnCode(transactionLog.getTmnCode());
            if (partner == null) {
                log.info("Partner for TmnCode: {}", transactionLog.getTmnCode());
            }
            IPNRequest request = new IPNRequest();
            request.setNeo_TmnCode(transactionLog.getTmnCode());
            request.setNeo_Amount(String.valueOf(transactionLog.getAmount()/100));
            request.setNeo_BankCode(transactionLog.getBankCode());
            request.setNeo_BankTranNo(generateBankTransactionNo(transactionLog.getBankCode()));
            request.setNeo_CardType("ATM");
            request.setNeo_PayDate(transactionLog.getPayDate());
            request.setNeo_OrderInfo(transactionLog.getOrderInfo());
            request.setNeo_TransactionNo(transactionLog.getTransactionNo());
            request.setNeo_ResponseCode(transactionLog.getResponseCode());
            request.setNeo_TransactionStatus(transactionLog.getTransactionStatus());
            request.setNeo_TxnRef(transactionLog.getTxnRef());
            request.setDomain(transactionLog.getDomain());
            blockingQueue.put(request);
            // Generate secure hash first
            String hashData = NeoUtils.buildQueryString(request.toMap());
            String secureHash = NeoUtils.hmacSHA512(partner.getSecretKey(), hashData);
            hashData += "&Neo_SecureHash=" + secureHash;
            String returnUrlNew = transactionLog.getReturnUrl() + "?" + hashData;
            log.info("Return URL: {}", returnUrlNew);
            return returnUrlNew;
        } catch (Exception e) {
            log.error("Error creating IPN callback for txnRef: {}", transactionLog.getTxnRef(), e);
            return "/wellcom";
        }
    }

    private static String generateBankTransactionNo(String bankCode) {
        if (bankCode == null || bankCode.equals("")) {
            bankCode = "NEOBANK";
        }
        return bankCode + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private String getPayDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
}
