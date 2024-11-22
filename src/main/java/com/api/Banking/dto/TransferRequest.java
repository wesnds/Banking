package com.api.Banking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferRequest {
    @Schema(name = "Sending user account number")
    private String senderAccountNumber;
    @Schema(name = "Receiving user account number")
    private String receiverAccountNumber;
    @Schema(name = "Amount to transfer")
    private BigDecimal amount;
}
