package com.neo.service.impl;

import com.neo.modal.NeoPaymentRequest;
import com.neo.service.VnpayService;
import org.springframework.stereotype.Service;

@Service
public class VnpayServiceImpl implements VnpayService {
    @Override
    public String createPaymentUrl(NeoPaymentRequest request) {
        return null;
    }
}
