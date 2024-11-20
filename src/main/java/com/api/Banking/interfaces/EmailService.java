package com.api.Banking.interfaces;

import com.api.Banking.dto.EmailDetails;

public interface EmailService {
    void sendEmailAlert(EmailDetails emailDetails);
}
