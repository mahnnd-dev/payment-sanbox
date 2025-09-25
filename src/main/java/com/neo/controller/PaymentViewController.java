package com.neo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.modal.NeoPaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PaymentViewController {
    private final ObjectMapper mapper;
    private final String supplierName = "https://neo.vn";

    // Trang chính (index.html)
    @GetMapping("/payment")
    public String paymentPage(@ModelAttribute NeoPaymentRequest request, Model model) throws JsonProcessingException {
        // Log để debug
        log.info("Payment request received: {}", request.getNeo_TxnRef());
        // Giả lập dữ liệu từ request hoặc query param
        String json = mapper.writeValueAsString(request);
        model.addAttribute("neoPaymentRequest", json);
        model.addAttribute("supplierName", supplierName);
        return "index"; // Template chính
    }

    // Trả về danh sách phương thức thanh toán (fragment)
    @GetMapping("/payment/methods")
    public String paymentMethods(Model model, @ModelAttribute NeoPaymentRequest request) throws JsonProcessingException {
        // Đảm bảo dữ liệu được truyền vào fragment
        String json = mapper.writeValueAsString(request);
        model.addAttribute("neoPaymentRequest", json);
        model.addAttribute("supplierName", supplierName);
        return "payment_methods"; // resources/templates/payment_methods.html
    }

    // Trả về form thanh toán (fragment)
    @GetMapping("/payment/form")
    public String paymentForm(Model model, @ModelAttribute NeoPaymentRequest request) throws JsonProcessingException {
        // Đảm bảo dữ liệu được truyền vào fragment
        String json = mapper.writeValueAsString(request);
        model.addAttribute("neoPaymentRequest", json);
        model.addAttribute("supplierName", supplierName);
        return "payment_form";
    }

    @GetMapping("/payment/result")
    public String paymentResult(Model model, String status) {
        // Đảm bảo dữ liệu được truyền vào fragment
        model.addAttribute("status", status);
        return "payment_result";
    }

    @GetMapping("/test")
    public String test() {
        return "payment";
    }
}