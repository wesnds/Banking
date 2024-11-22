package com.api.Banking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    @Schema(name = "Type of transaction")
    private String transactionType;
    @Schema(name = "User account number")
    private String accountNumber;
    @Schema(name = "Transaction status")
    private String status;
    @Schema(name = "Transaction amount")
    private BigDecimal amount;
}
