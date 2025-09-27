package com.neo.controller;

import com.neo.dto.QueryRequest;
import com.neo.dto.QueryResponse;
import com.neo.service.QueryTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/neo-payment")
public class QueryTransactionController {
    private final QueryTransactionService queryTransactionService;

    @PostMapping(value = "/query", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QueryResponse> queryTransaction(@RequestBody QueryRequest request) {
        QueryResponse response = queryTransactionService.queryTransaction(request);
        return ResponseEntity.ok(response);
    }
}
