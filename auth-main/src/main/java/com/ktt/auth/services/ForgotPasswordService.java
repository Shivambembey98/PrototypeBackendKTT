package com.ktt.auth.services;

import com.ktt.auth.entities.User;
import com.ktt.auth.exceptions.InvalidJwtException;
import com.ktt.auth.repositories.ForgotPasswordRepo;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ForgotPasswordService {
    private static final long EXPIRE_TOKEN=30;

    @Autowired
    private ForgotPasswordRepo repo;
    @Autowired
    private EmailService emailService;

//    public String forgotPass(String email){
//        Optional<User> userOptional = Optional.ofNullable(repo.findByLogin("ankit"));
//
//        if(!userOptional.isPresent()){
//            return "Invalid email id.";
//        }
//
//        User user=userOptional.get();
//        user.setToken(generateToken());
//        user.setTokenCreationDate(LocalDateTime.now());
//
//        user=repo.save(user);
//        return user.getToken();
//    }

    public String forgotPass(String email, String companyCode) throws MessagingException, UnsupportedEncodingException {
        Optional<User> userOptional = Optional.ofNullable(repo.findByEmailIdAndCompanyCode(email, companyCode));

        System.out.println(userOptional);
        if(!userOptional.isPresent()){
            return "Invalid email id.";
        }

        User user=userOptional.get();
        user.setToken(generateToken());

        String otp = generateOTP();
        String Content = "Your OTP is: " + otp + "\n\nPlease use this OTP to reset your password.";


        user.setTokenCreationDate(LocalDateTime.now());
        emailService.sendEmail(email,"Password Reset",Content);
        user.setOtp(otp);
        user=repo.save(user);
        return user.getToken();
    }

    public String resetPass(String token, String password,String otp){
        Optional<User> userOptional= Optional.ofNullable(repo.findByToken(token));

        if(!userOptional.isPresent()){
            return "Invalid token";
        }
        LocalDateTime tokenCreationDate = userOptional.get().getTokenCreationDate();

        if (isTokenExpired(tokenCreationDate)) {
            return "Token expired.";

        }

        User user = userOptional.get();
        if(!otp.equals(user.getOtp())){

            return "Invalid OTP";
        }
        else {

            BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
            boolean isValid = bcrypt.matches(password, user.getPassword()) || bcrypt.matches(password, user.getPassword2()) || bcrypt.matches(password, user.getPassword3());
            if (isValid) {
                throw new InvalidJwtException("This Password has been used before. Please try with a new one.");
            }
            user.setPassword3(user.getPassword2());
            user.setPassword2(user.getPassword());
            user.setPassword(new BCryptPasswordEncoder().encode(password));
            user.setPasswordUpdateDate(LocalDateTime.now());
            user.setUpdateDate(LocalDateTime.now());
            //user.setOtp(null);
            user.setNumberOfAttempts(0);
            user.setAccountStatus("Active");
            user.setToken(null);
            user.setTokenCreationDate(null);
            repo.save(user);
        }

        return "Your password is updated successfully.";

    }

    private String generateToken() {
        StringBuilder token = new StringBuilder();

        return token.append(UUID.randomUUID().toString())
                .append(UUID.randomUUID().toString()).toString();
    }

    private boolean isTokenExpired(final LocalDateTime tokenCreationDate) {

        LocalDateTime now = LocalDateTime.now();
        Duration diff = Duration.between(tokenCreationDate, now);

        return diff.toMinutes() >=EXPIRE_TOKEN;
    }

    public String generateOTP() {
        // Using SecureRandom for cryptographically strong random numbers
        SecureRandom secureRandom = new SecureRandom();

        // Generate 6-digit OTP
        int otp = 100000 + secureRandom.nextInt(900000);

        return String.valueOf(otp);
    }

}