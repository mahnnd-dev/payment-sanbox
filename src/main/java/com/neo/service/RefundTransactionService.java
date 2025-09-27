package com.neo.service;

import com.neo.dto.RefundRequest;
import com.neo.modal.TransactionLog;
import com.neo.repository.TransactionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundTransactionService {
    private final TransactionLogRepository logRepository;
    private final ValidateService validateService;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public Map<String, Object> refundTransaction(RefundRequest request) {
        if (!validateService.validateRequestRefundSecureHash(request)) {
            return Collections.emptyMap();
        }
        List<TransactionLog> transactionLogList = logRepository.findAllByTxnRef(request.getNeo_TxnRef());
        log.info("{}", transactionLogList);
//        TransactionLog log = new TransactionLog();
//        log.setCommand(request.getNeo_Command());
//        log.setRequestId(request.getNeo_RequestId());
//        log.setVersion(request.getNeo_Version());
//        log.setTmnCode(request.getNeo_TmnCode());
//        log.setTxnRef(request.getNeo_TxnRef());
//        log.setOrderInfo(request.getNeo_OrderInfo());
//        log.setTransactionDate(request.getNeo_TransactionDate());
//        log.setCreateDate(request.getNeo_CreateDate());
//        log.setIpAddr(request.getNeo_IpAddr());
//        log.setResponseCode(request.getStatus());
//        log.setResponseMessage(request.getStatusMessage());
//        log.setTransactionNo(request.getNeo_TransactionNo());
//        log.setPayDate(null);
//        log.setTransactionType("NEOPAY");
//        log.setRefundAmount(null);
//        log.setTransactionStatus(null);
//        log.setAmount(request.getNeo_Amount());
//        log.setBankCode(null);
//        logRepository.save(log);
        return new HashMap<>();
    }
}
