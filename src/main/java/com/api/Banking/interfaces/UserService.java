package com.api.Banking.interfaces;

import com.api.Banking.dto.BankResponse;
import com.api.Banking.dto.UserRequest;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest);
}