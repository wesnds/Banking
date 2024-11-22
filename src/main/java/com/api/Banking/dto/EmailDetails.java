package com.api.Banking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailDetails {
    @Schema(name = "Email sender")
    private String recipient;
    @Schema(name = "Email text")
    private String messageBody;
    @Schema(name = "Email subject")
    private String subject;
    @Schema(name = "Email attachment")
    private String attachment;
}
