package com.api.Banking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankResponse {
    @Schema(name = "Bank response code")
    private String responseCode;
    @Schema(name = "Bank response message")
    private String responseMessage;
    @Schema(name = "User account info")
    private AccountInfo accountInfo;
}
