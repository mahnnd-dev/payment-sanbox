package com.neo.controller;

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

    private final String supplierName = "https://neo.vn";

    // Trang chính (index.html)
    @GetMapping("/payment")
    public String paymentPage(@ModelAttribute NeoPaymentRequest request, Model model) {
        // Log để debug
        log.info("Payment request received: {}", request);
        // Giả lập dữ liệu từ request hoặc query param
        model.addAttribute("neoPaymentRequest", request);
        model.addAttribute("supplierName", supplierName);
        return "index"; // Template chính
    }

    // Trả về danh sách phương thức thanh toán (fragment)
    @GetMapping("/payment/methods")
    public String paymentMethods(Model model, @ModelAttribute NeoPaymentRequest request) {
        // Đảm bảo dữ liệu được truyền vào fragment
        model.addAttribute("neoPaymentRequest", request);
        model.addAttribute("supplierName", supplierName);
        return "payment_methods"; // resources/templates/payment_methods.html
    }

    // Trả về form thanh toán (fragment)
    @GetMapping("/payment/form")
    public String paymentForm(Model model, @ModelAttribute NeoPaymentRequest request) {
        // Đảm bảo dữ liệu được truyền vào fragment
        model.addAttribute("neoPaymentRequest", request);
        model.addAttribute("supplierName", supplierName);
        return "payment_form";
    }

    @GetMapping("/test")
    public String test() {
        return "payment";
    }
}