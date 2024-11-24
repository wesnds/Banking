package com.api.Banking.controller;

import com.api.Banking.entity.Transaction;
import com.api.Banking.service.BankStatementService;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/bank-statement")
public class TransactionController {

    @Autowired
    private BankStatementService bankStatementService;

    @GetMapping
    public List<Transaction> generateBankStatement(@RequestParam String accountNumber, @RequestParam String startDate, @RequestParam String endDate ) throws FileNotFoundException, DocumentException {
        return bankStatementService.generateStatement(accountNumber, startDate, endDate);
    }
}
