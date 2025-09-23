package com.neo.service;

import com.neo.modal.Bank;
import org.springframework.stereotype.Service;

import java.util.List;

public interface BankService {
    List<Bank> getAllBanks();
}
