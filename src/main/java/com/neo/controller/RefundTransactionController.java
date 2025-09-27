package com.neo.controller;

import com.neo.dto.RefundRequest;
import com.neo.dto.RefundResponse;
import com.neo.service.RefundTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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

    @PostMapping(value = "/refund", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RefundResponse> refundTransaction(@RequestBody RefundRequest request) {
        RefundResponse response = refundTransaction.refundTransaction(request);
        return ResponseEntity.ok(response);
    }
}
