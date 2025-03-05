package com.ktt.auth.controllers;

import com.ktt.auth.dtos.Request;
import com.ktt.auth.entities.User;
import com.ktt.auth.services.ForgotPasswordService;
import com.ktt.auth.services.impl.AuthService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.ktt.auth.config.auth.TokenProvider;
import com.ktt.auth.dtos.SignInDto;
import com.ktt.auth.dtos.SignUpDto;
import com.ktt.auth.dtos.JwtDto;

import java.io.UnsupportedEncodingException;

@CrossOrigin()
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  @Autowired
  private AuthenticationManager authenticationManager;
  @Autowired
  private AuthService service;
  @Autowired
  private ForgotPasswordService forgotPasswordService;
  @Autowired
  private TokenProvider tokenService;

  @PostMapping("/signup")
  public ResponseEntity<?> signUp(@RequestBody @Valid SignUpDto data) {
    UserDetails user = service.signUp(data);
    return ResponseEntity.status(HttpStatus.CREATED).body(user.getUsername());
  }

  @PostMapping("/userChecker")
  public ResponseEntity<?> usernameChecker(@RequestBody @Valid Request data) {
    return service.loadUserByUsernameAndCompanyCode(data.login(),data.companyCode());
  }

  @PostMapping("/signin")
  public ResponseEntity<JwtDto> signIn(@RequestBody @Valid SignInDto data) {

    String lowerCaseUserName = data.login().toLowerCase();

    if(!service.validateByUsrAndPasswordAndCmpCd(lowerCaseUserName, data.password(), data.companyCode())){
      throw new BadCredentialsException("Invalid Credentials");
    }

    service.isAccountActive(lowerCaseUserName, data.companyCode());

    var usernamePassword = new UsernamePasswordAuthenticationToken(lowerCaseUserName, data.password());

    var authUser = authenticationManager.authenticate(usernamePassword);

    var accessToken = tokenService.generateAccessToken((User) authUser.getPrincipal());

    return ResponseEntity.ok(new JwtDto(accessToken));

  }

  @PostMapping("/forgot-password")
  public String forgotPasss(@RequestParam String email,@RequestParam String companyCode) throws MessagingException, UnsupportedEncodingException {
    String response = forgotPasswordService.forgotPass(email,companyCode);

    if(!response.startsWith("Invalid")){
      response= "http://localhost:8080/api/v1/auth/reset-password?token=" + response;
    }
    return response;
  }

  @PutMapping("/reset-password")
  public String resetPass(@RequestParam String token, @RequestParam String password, @RequestParam String otp) {
    return forgotPasswordService.resetPass(token, password, otp);
  }

}
