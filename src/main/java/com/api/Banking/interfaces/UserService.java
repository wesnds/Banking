package com.api.Banking.interfaces;

import com.api.Banking.dto.BankResponse;
import com.api.Banking.dto.CreditDebitRequest;
import com.api.Banking.dto.EnquiryRequest;
import com.api.Banking.dto.UserRequest;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest);
    BankResponse balanceEnquiry(EnquiryRequest enquiryRequest);
    String nameEnquiry(EnquiryRequest request);
    BankResponse creditAccount(CreditDebitRequest creditDebitRequest);
}
