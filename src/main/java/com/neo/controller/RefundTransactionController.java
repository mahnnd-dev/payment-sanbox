package com.neo.controller;

import com.neo.dto.RefundRequest;
import com.neo.service.RefundTransactionService;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<Map<String, Object>> refundTransaction(
            @Valid @RequestBody RefundRequest request,
            HttpServletRequest httpServletRequest) {
        String ipAddress = httpServletRequest.getRemoteAddr();
        Map<String, Object> response = refundTransaction.refundTransaction(request, ipAddress);
        return ResponseEntity.ok(response);
    }
}
