package com.neo.service;

import com.neo.modal.NeoPaymentRequest;


public interface VnpayService {
    String createPaymentUrl(NeoPaymentRequest request);
}
