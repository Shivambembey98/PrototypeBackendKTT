package com.ktt.auth.controllers;

import com.ktt.auth.dtos.OtpRequest;
import com.ktt.auth.dtos.OtpValidationRequest;
import com.ktt.auth.dtos.Response;
import com.ktt.auth.services.EmailService;
import com.ktt.auth.services.impl.OtpService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("api/v1/otp")
@AllArgsConstructor
public class OtpController {

    private final OtpService otpService;

    private final EmailService emailService;

    @PostMapping("/sendOtp")
    public Response sendOtp(@RequestBody OtpRequest otpRequest){
        return otpService.sendOtp(otpRequest);
    }

    @PostMapping("/validateOtp")
    public Response validateOtp(@RequestBody OtpValidationRequest otpValidationRequest){
        return otpService.validateOtp(otpValidationRequest);
    }


}
