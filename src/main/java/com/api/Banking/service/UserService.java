package com.api.Banking.service;

import com.api.Banking.config.JwtTokenProvider;
import com.api.Banking.dto.*;
import com.api.Banking.entity.Enums.Role;
import com.api.Banking.entity.User;
import com.api.Banking.repository.UserRepository;
import com.api.Banking.utils.AccountUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
@AllArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    TransactionService transactionService;

    @Autowired
    EmailService emailService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    public BankResponse createAccount(UserRequest userRequest) {

        if(userRepository.existsByEmail(userRequest.getEmail())){
            BankResponse response = BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .status("ACTIVE")
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .role(Role.valueOf("ROLE_ADMIN"))
                .build();

        User savedUser = userRepository.save(newUser);
        //send email alert
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Congrats! Your account has been successfully created\n " +
                        "Your account details: \n" +
                        "Account name: " + savedUser.getFirstName() + " " + savedUser.getLastName() + "\n" +
                        "Account number: " + savedUser.getAccountNumber()
                )
                .build();
        emailService.sendEmailAlert(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATED_SUCESS)
                .responseMessage((AccountUtils.ACCOUNT_CREATED_MESSAGE))
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName())
                        .build())
                .build();
    }

    public BankResponse login(LoginDTO loginDTO){
        Authentication authentication = null;
        authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
        );

        EmailDetails loginAlert = EmailDetails.builder()
                .subject("You're logged in!")
                .recipient(loginDTO.getEmail())
                .messageBody("You logged into your account. If you did not initiate this request, please contact your bank!")
                .build();
        emailService.sendEmailAlert(loginAlert);

        return BankResponse.builder()
                .responseCode("Login Success")
                .responseMessage(jwtTokenProvider.generateToken(authentication))
                .build();
    }

    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
        boolean accountExist = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if(!accountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(enquiryRequest.getAccountNumber())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName())
                        .build())
                .build();
    }

    public String nameEnquiry(EnquiryRequest enquiryRequest) {
        boolean accountExist = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if(!accountExist){
           return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }

        User user = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return user.getFirstName() + " " + user.getLastName();
    }

    public BankResponse creditAccount(CreditDebitRequest creditDebitRequest) {
        boolean accountExist = userRepository.existsByAccountNumber(creditDebitRequest.getAccountNumber());
        if(!accountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToCredit = userRepository.findByAccountNumber(creditDebitRequest.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(creditDebitRequest.getAmount()));
        userRepository.save(userToCredit);

        //save transaction
        TransactionDTO transactionDTO = TransactionDTO.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .transactionType("CREDIT")
                .amount(creditDebitRequest.getAmount())
                .build();

        transactionService.saveTransaction(transactionDTO);

        return BankResponse.builder()
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName())
                        .accountBalance(userToCredit.getAccountBalance())
                        .accountNumber(creditDebitRequest.getAccountNumber())
                        .build())
                .build();
    }

    public BankResponse debitAccount(CreditDebitRequest creditDebitRequest) {
        boolean accountExist = userRepository.existsByAccountNumber(creditDebitRequest.getAccountNumber());
        if (!accountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToDebit = userRepository.findByAccountNumber(creditDebitRequest.getAccountNumber());
        BigInteger availableBalance =userToDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount = creditDebitRequest.getAmount().toBigInteger();
        if ( availableBalance.intValue() < debitAmount.intValue()){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        else {
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(creditDebitRequest.getAmount()));
            userRepository.save(userToDebit);

            TransactionDTO transactionDTO = TransactionDTO.builder()
                    .accountNumber(userToDebit.getAccountNumber())
                    .transactionType("CREDIT")
                    .amount(creditDebitRequest.getAmount())
                    .build();

            transactionService.saveTransaction(transactionDTO);

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountNumber(creditDebitRequest.getAccountNumber())
                            .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName())
                            .accountBalance(userToDebit.getAccountBalance())
                            .build())
                    .build();
        }
    }

    public BankResponse transfer(TransferRequest transferRequest){
        boolean receiverAccountExists = userRepository.existsByAccountNumber(transferRequest.getReceiverAccountNumber());

        User receiverAccount = userRepository.findByAccountNumber(transferRequest.getReceiverAccountNumber());

        if(!receiverAccountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User senderAccount = userRepository.findByAccountNumber(transferRequest.getSenderAccountNumber());
        if(transferRequest.getAmount().compareTo(senderAccount.getAccountBalance()) > 0){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        senderAccount.setAccountBalance(senderAccount.getAccountBalance().subtract(transferRequest.getAmount()));
        userRepository.save(senderAccount);

        EmailDetails debitAlert = EmailDetails.builder()
                .subject("DEBIT ALERT")
                .recipient(senderAccount.getEmail())
                .messageBody("The sum of $" + transferRequest.getAmount() + " has been transferred to: " + receiverAccount.getFirstName() + " " + receiverAccount.getLastName() +  "Your current balance is: " + senderAccount.getAccountBalance())
                .build();
        emailService.sendEmailAlert(debitAlert);

        receiverAccount.setAccountBalance(senderAccount.getAccountBalance().add(transferRequest.getAmount()));
        userRepository.save(receiverAccount);

        EmailDetails creditAlert = EmailDetails.builder()
                .subject("CREDIT ALERT")
                .recipient(senderAccount.getEmail())
                .messageBody(senderAccount.getFirstName() + " " + senderAccount.getLastName() + "Has transferred $" + transferRequest.getAmount() + " to your account! Your current balance is: $" + receiverAccount.getAccountBalance())
                .build();
        emailService.sendEmailAlert(creditAlert);

        //save transfer
        TransactionDTO transactionDTO = TransactionDTO.builder()
                .accountNumber(receiverAccount.getAccountNumber())
                .transactionType("CREDIT")
                .amount(transferRequest.getAmount())
                .build();

        transactionService.saveTransaction(transactionDTO);

        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESS_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESS_MESSAGE)
                .accountInfo(null)
                .build();
    }
}
