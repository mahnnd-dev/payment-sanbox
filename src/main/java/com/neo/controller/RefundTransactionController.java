package com.neo.controller;

import com.neo.dto.RefundRequest;
import com.neo.service.RefundTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/neo-payment")
public class RefundTransactionController {
    private final RefundTransactionService refundTransaction;

    @PostMapping("/refund")
    public ResponseEntity<Map<String, Object>> refundTransaction(@Valid @RequestBody RefundRequest request) {
        Map<String, Object> response = refundTransaction.refundTransaction(request);
        return ResponseEntity.ok(response);
    }
}
