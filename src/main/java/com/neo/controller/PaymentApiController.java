package com.neo.controller;

import com.neo.modal.Bank;
import com.neo.modal.NeoPaymentRequest;
import com.neo.service.BankService;
import com.neo.service.VnpayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentApiController {
    private final BankService bankService;
    private final VnpayService vnpayService;

    @GetMapping("/banks")
    public List<Bank> getBanks() {
        return bankService.getAllBanks();
    }

    @PostMapping("/process")
    public ResponseEntity<Map<String, String>> processPayment(@RequestBody NeoPaymentRequest request) {
        String paymentUrl = vnpayService.createPaymentUrl(request);
        return ResponseEntity.ok(Map.of("redirectUrl", paymentUrl));
    }
}
