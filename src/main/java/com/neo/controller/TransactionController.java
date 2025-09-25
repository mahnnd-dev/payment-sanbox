package com.neo.controller;

import com.neo.dto.TransactionRequest;
import com.neo.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/neo-payment")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/save-transaction")
    public ResponseEntity<?> saveTransaction(@RequestBody TransactionRequest request) {
        transactionService.saveTransaction(request);
        return ResponseEntity.ok().body(
                Map.of("success", true, "message", "Transaction saved", "status", request.getStatus())
        );
    }
}
