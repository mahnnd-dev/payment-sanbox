package com.neo.controller;

import com.neo.dto.*;
import com.neo.modal.Banker;
import com.neo.service.QueryTransactionService;
import com.neo.service.RefundTransactionService;
import com.neo.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/neo-payment")
public class TransactionController {
    private final TransactionService transactionService;
    private final QueryTransactionService queryTransactionService;
    private final RefundTransactionService refundTransaction;

    @PostMapping("/save-transaction")
    public ResponseEntity<?> saveTransaction(@RequestBody TransactionRequest request) {
        String url = transactionService.saveTransaction(request);
        return ResponseEntity.ok().body(
                Map.of("success", true, "message", "Transaction saved", "status", request.getStatus(), "url", url));
    }

    @PostMapping(value = "/query", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QueryResponse> queryTransaction(@RequestBody QueryRequest request) {
        QueryResponse response = queryTransactionService.queryTransaction(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/refund", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RefundResponse> refundTransaction(@RequestBody RefundRequest request) {
        RefundResponse response = refundTransaction.refundTransaction(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/validate-card", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validateCard(@RequestBody Banker banker) {
        Map<String, Serializable > check = transactionService.validateCard(banker);
        return ResponseEntity.ok().body(check);
    }
}
