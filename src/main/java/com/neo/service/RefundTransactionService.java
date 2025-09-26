package com.neo.service;

import com.neo.dto.RefundRequest;
import com.neo.repository.RefundTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundTransactionService {

    private final RefundTransactionRepository refundRepo;

    public Map<String, Object> refundTransaction(RefundRequest request, String ipAddress) {
        return Collections.emptyMap();
    }
}
