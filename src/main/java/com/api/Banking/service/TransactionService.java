package com.api.Banking.service;

import com.api.Banking.dto.TransactionDTO;
import com.api.Banking.entity.Transaction;
import com.api.Banking.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    @Autowired
    TransactionRepository transactionRepository;

    public void saveTransaction(TransactionDTO transactionDto){
        Transaction transaction = Transaction.builder()
                .transactionType(transactionDto.getTransactionType())
                .receiverAccountNumber(transactionDto.getAccountNumber())
                .amount(transactionDto.getAmount())
                .status("SUCCESS")
                .build();

        transactionRepository.save(transaction);

        System.out.println("Transaction saved!");
    }
}
