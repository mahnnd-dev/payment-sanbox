package com.neo.service;

import com.neo.dto.QueryRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class QueryTransactionService {
    public Map<String, Object> queryTransaction(QueryRequest request, String ipAddress) {
        return Collections.emptyMap();
    }
}
