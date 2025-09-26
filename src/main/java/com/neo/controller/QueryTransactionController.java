package com.neo.controller;

import com.neo.dto.QueryRequest;
import com.neo.service.QueryTransactionService;
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
public class QueryTransactionController {
    private final QueryTransactionService queryTransactionService;

    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> queryTransaction(
            @Valid @RequestBody QueryRequest request,
            HttpServletRequest httpServletRequest) {
        // In a real app, you might want a more robust way to get the client IP
        String ipAddress = httpServletRequest.getRemoteAddr();
        Map<String, Object> response = queryTransactionService.queryTransaction(request, ipAddress);
        return ResponseEntity.ok(response);
    }
}
