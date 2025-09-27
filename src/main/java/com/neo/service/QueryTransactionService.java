package com.neo.service;

import com.neo.dto.QueryRequest;
import com.neo.dto.QueryResponse;
import com.neo.modal.TransactionLog;
import com.neo.repository.TransactionLogRepository;
import com.neo.util.NeoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueryTransactionService {
    @Value("${neo.payment.secret-key}")
    private String secretKey;

    private final TransactionLogRepository logRepository;
    private final ValidateService validateService;

    public QueryResponse queryTransaction(QueryRequest request) {
        if (!validateService.validateRequestQuerySecureHash(request)) {
            return null;
        }
        TransactionLog transactionLog = logRepository.findAllByTxnRef(request.getNeoTxnRef());
        QueryResponse queryResponse = new QueryResponse();
        queryResponse.setNeoResponseId(UUID.randomUUID().toString());
        queryResponse.setNeoCommand("query");
        queryResponse.setNeoTmnCode(transactionLog.getTmnCode());
        queryResponse.setNeoAmount(transactionLog.getAmount());
        queryResponse.setNeoOrderInfo(transactionLog.getOrderInfo());
        queryResponse.setNeoResponseCode(transactionLog.getResponseCode());
        queryResponse.setNeoMessage(transactionLog.getResponseMessage());
        queryResponse.setNeoBankCode(transactionLog.getBankCode());
        queryResponse.setNeoPayDate(transactionLog.getPayDate());
        queryResponse.setNeoTransactionNo(transactionLog.getTransactionNo());
        queryResponse.setNeoTransactionType(queryResponse.getNeoTransactionType());
        queryResponse.setNeoTransactionStatus(queryResponse.getNeoTransactionStatus());
        queryResponse.setNeoPromotionCode(queryResponse.getNeoPromotionCode());
        queryResponse.setNeoPromotionAmount(queryResponse.getNeoPromotionAmount());
        String hashData = NeoUtils.buildQueryString(request.toMap());
        String calculatedHash = NeoUtils.hmacSHA512(secretKey, hashData);
        queryResponse.setNeoSecureHash(calculatedHash);
        return queryResponse;
    }
}
