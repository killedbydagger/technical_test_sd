package com.temp.demo.service;

import com.temp.demo.dto.EmailVerificationDTO;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Async("emailTaskExecutor")
    public void sendEmailVerificationForgetPasswordAsync(EmailVerificationDTO email) {
        sendEmailVerificationForgetPassword(email);
    }


    public void sendEmailVerificationForgetPassword(EmailVerificationDTO verificationDTO) {

    }
}
