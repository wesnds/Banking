package com.api.Banking.controller;

import com.api.Banking.dto.BankResponse;
import com.api.Banking.dto.CreditDebitRequest;
import com.api.Banking.dto.EnquiryRequest;
import com.api.Banking.dto.UserRequest;
import com.api.Banking.service.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/api/user")
public class UserController {

    @Autowired
    private UserServiceImpl userService;


    @PostMapping("new-user")
    public ResponseEntity<BankResponse> createAccount(@Valid @RequestBody UserRequest userRequest){
       BankResponse createdUser = userService.createAccount(userRequest);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(createdUser.getAccountInfo().getAccountName()).toUri();

        return ResponseEntity.created(uri).body(createdUser);
    }

    @GetMapping("name-enquiry")
    public String nameEnquiry(@RequestBody EnquiryRequest enquiryRequest){
        return userService.nameEnquiry(enquiryRequest);
    }

    @GetMapping("balance-enquiry")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest enquiryRequest){
        return userService.balanceEnquiry(enquiryRequest);
    }

    @PostMapping("credit")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request){
        return userService.creditAccount(request);
    }

    @PostMapping("debit")
    public BankResponse debitAccount(@RequestBody CreditDebitRequest request){
        return userService.debitAccount(request);
    }
}
