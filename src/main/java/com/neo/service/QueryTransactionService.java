package com.neo.service;

import com.neo.dto.QueryRequest;
import com.neo.modal.TransactionLog;
import com.neo.repository.TransactionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueryTransactionService {

    private final TransactionLogRepository logRepository;
    private final ValidateService validateService;

    public Map<String, Object> queryTransaction(QueryRequest request) {
        if (!validateService.validateRequestQuerySecureHash(request)) {
            return Collections.emptyMap();
        }

        return Collections.emptyMap();
    }
}
