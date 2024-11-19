package com.api.Banking.controller;

import com.api.Banking.dto.BankResponse;
import com.api.Banking.dto.UserRequest;
import com.api.Banking.entity.User;
import com.api.Banking.service.UserService;
import com.api.Banking.service.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/user")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

//    @GetMapping("all-users")

    @PostMapping("new-user")
    public ResponseEntity<BankResponse> createAccount(@Valid @RequestBody UserRequest userRequest){
       BankResponse createdUser = userService.createAccount(userRequest);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(createdUser.getAccountInfo().getAccountName()).toUri();

        return ResponseEntity.created(uri).body(createdUser);
    }

}
