package com.ktt.auth.services.impl;

import com.ktt.auth.dtos.OtpRequest;
import com.ktt.auth.dtos.OtpResponse;
import com.ktt.auth.dtos.OtpValidationRequest;
import com.ktt.auth.dtos.Response;
import com.ktt.auth.entities.Otp;
import com.ktt.auth.repositories.OtpRepository;
import com.ktt.auth.services.EmailService;
import com.ktt.auth.utils.AppUtils;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class OtpService {

    private final OtpRepository otpRepository;
    private final EmailService emailService;

    public Response sendOtp(OtpRequest otpRequest) {

        Otp existingOtp = otpRepository.findByEmailIdAndCompanyCode(otpRequest.getEmailId(),otpRequest.getCompanyCode());
        if(existingOtp != null){
            otpRepository.delete(existingOtp);
        }

        String otp = AppUtils.generateOTP();
        log.info("Otp: {}",otp);

        final String subject = "KTT (Do Not Share: OTP)";
        String content = "<p>OTP for your account is "+ otp + " and will expire in next 2 minutes </p>" ;

        otpRepository.save(Otp.builder()
                .emailId(otpRequest.getEmailId())
                .otp(otp)
                .companyCode(otpRequest.getCompanyCode())
                .expiresAt(LocalDateTime.now().plusMinutes(2)).build());

        try {
            emailService.sendEmail(otpRequest.getEmailId(), subject, content);
        } catch (UnsupportedEncodingException | MessagingException e) {
            System.out.println(e.getStackTrace());
        }
        return Response.builder()
                .statusCode(200)
                .responseMessage("SUCCESS")
                .build();
    }

    public Response validateOtp(OtpValidationRequest otpValidationRequest){
        Otp otp = otpRepository.findByEmailIdAndCompanyCode(otpValidationRequest.getEmailId(),otpValidationRequest.getCompanyCode());
        log.info("Email : {} ",otpValidationRequest.getEmailId() , "companyCode : {} ",otpValidationRequest.getCompanyCode());
        if (otp == null) {
        return Response.builder()
                .statusCode(400)
                .responseMessage("You have not sent an Otp")
                .build();
        }
        if(otp.getExpiresAt().isBefore(LocalDateTime.now())){
            return Response.builder()
                    .statusCode(400)
                    .responseMessage("Otp has expired")
                    .build();
        }
        if(!otp.getOtp().equals(otpValidationRequest.getOtp())){
            return Response.builder()
                    .statusCode(400)
                    .responseMessage("Otp is Invalid").build();
        }
    return Response.builder()
            .statusCode(200)
            .responseMessage("Otp is verified")
            .otpResponse(OtpResponse.builder()
                    .isOtpValid(true).build())
            .build();
    }
}
