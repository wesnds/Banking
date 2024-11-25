package com.api.Banking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    @Schema(name = "User first name")
    private String firstName;
    @Schema(name = "User last name")
    private String lastName;
    @Schema(name = "User email")
    private String email;
    @Schema(name = "User phone number")
    private String phoneNumber;
    @Schema(name = "User alternative phone number")
    private String alternativePhoneNumber;
    @Schema(name = "User gender")
    private String gender;
    @Schema(name = "User address")
    private String address;
    @Schema(name = "User state of birth")
    private String stateOfOrigin;
    @Schema(name = "User password")
    private String password;
}
