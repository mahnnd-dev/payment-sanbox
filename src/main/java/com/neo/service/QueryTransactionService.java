package com.neo.service;

import com.neo.dto.QueryRequest;
import com.neo.dto.QueryResponse;
import com.neo.modal.TransactionLog;
import com.neo.repository.TransactionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueryTransactionService {

    private final TransactionLogRepository logRepository;
    private final ValidateService validateService;

    public QueryResponse queryTransaction(QueryRequest request) {
        Map<String, Object> map = new HashMap<>();
        if (!validateService.validateRequestQuerySecureHash(request)) {
            return null;
        }
        TransactionLog transactionLog = logRepository.findAllByTxnRef(request.getNeoTxnRef());
        QueryResponse queryResponse = new QueryResponse();
        queryResponse.setNeo_ResponseId(request.getNeoRequestId());
        queryResponse.setNeo_Command("query");
        queryResponse.setNeo_TmnCode(transactionLog.getTmnCode());
        queryResponse.setNeo_Amount(transactionLog.getAmount());
        queryResponse.setNeo_OrderInfo(transactionLog.getOrderInfo());
        queryResponse.setNeo_ResponseCode(transactionLog.getResponseCode());
        queryResponse.setNeo_Message(transactionLog.getResponseMessage());
        queryResponse.setNeo_BankCode(transactionLog.getBankCode());
        queryResponse.setNeo_PayDate(transactionLog.getPayDate());
        queryResponse.setNeo_TransactionNo(Long.valueOf(transactionLog.getTransactionNo()));
        queryResponse.setNeo_TransactionType(queryResponse.getNeo_TransactionType());
        queryResponse.setNeo_TransactionStatus(queryResponse.getNeo_TransactionStatus());
        queryResponse.setNeo_PromotionCode(queryResponse.getNeo_PromotionCode());
        queryResponse.setNeo_PromotionAmount(queryResponse.getNeo_PromotionAmount());
        return queryResponse;
    }
}
