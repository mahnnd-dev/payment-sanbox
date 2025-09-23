package com.neo.service.impl;

import com.neo.modal.Bank;
import com.neo.service.BankService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BankServiceImpl implements BankService {

    @Override
    public List<Bank> getAllBanks() {
        Bank bank = new Bank("bank", "Vietcombank", "VCB");
        List<Bank> bankList = new ArrayList<>();
        bankList.add(bank);
        return bankList;
    }
}
