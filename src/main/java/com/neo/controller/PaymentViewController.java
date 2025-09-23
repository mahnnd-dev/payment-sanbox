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

    // Trang chính (index.html)
    @GetMapping("/payment")
    public String paymentPage(@ModelAttribute NeoPaymentRequest request, Model model) {
        // request lúc này đã được Spring bind từ query param vào
        model.addAttribute("supplierName", "manhnd");
        return "index"; // Thymeleaf template
    }

    // Trả về danh sách phương thức thanh toán (fragment)
    @GetMapping("/payment/methods")
    public String paymentMethods() {
        return "payment_methods"; // resources/templates/payment_methods.html
    }

    // Trả về form thanh toán (fragment)
    @GetMapping("/payment/form")
    public String paymentForm() {
        return "payment_form";
    }

    @GetMapping("/test")
    public String test() {
        return "payment";
    }
}
