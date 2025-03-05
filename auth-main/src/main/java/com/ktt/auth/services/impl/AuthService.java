package com.ktt.auth.services.impl;

import com.ktt.auth.dtos.SignUpDto;
import com.ktt.auth.entities.User;
import com.ktt.auth.exceptions.InvalidJwtException;
import com.ktt.auth.repositories.UserCustomRepository;
import com.ktt.auth.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AuthService implements UserDetailsService  {

  @Autowired
  UserRepository userRepository;

  @Autowired
  UserCustomRepository userCustomRepository;


  @Override
  public UserDetails loadUserByUsername(String username) {
    return userRepository.findByLogin(username);
  }

  public ResponseEntity<?> loadUserByUsernameAndCompanyCode(String username, String companyCode) {
    UserDetails user = userRepository.findByLoginAndCompanyCode(username.toLowerCase(),companyCode);
    if (user != null) {
      throw new InvalidJwtException("Username already exists");
    }
    return ResponseEntity.ok("Username Available");
  }

  public ResponseEntity<?> loadUserByEmailIdAndCompanyCode(String emailId, String companyCode) {
    UserDetails user = userRepository.findByEmailIdAndCompanyCode(emailId.toLowerCase(),companyCode);
    if (user != null) {
      throw new InvalidJwtException("User already exists with this email. One EmailId can only be associated with one account.");
    }
    return ResponseEntity.ok("EmailId is Available");
  }

  public Boolean validateByUsrAndPasswordAndCmpCd(String login,String password, String companyCode) {
    String encryptPass = userRepository.getPassword(login,companyCode);
    System.out.println(encryptPass);
    if(encryptPass == null){
      System.out.println("Invalid");
    return false;
    }

    if (userRepository.getAccountStatus(login, companyCode).equals("Blocked")) {
      throw new InvalidJwtException("Your account is blocked due to multiple unsuccessful attempts. Please contact admin");
    }

    BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
    boolean isValid = bcrypt.matches(password, encryptPass);
    if(!isValid){
      userCustomRepository.updateNoOfAttempt(login,companyCode);
      return false;
    }
    userCustomRepository.updateAccountStatus(login,companyCode);
    return true;
  }

  public void isAccountActive(String login, String companyCode){
      if(!userRepository.getMailVerificationStatus(login, companyCode))
          throw new InvalidJwtException("Mail is not yet verified. Please verify to login");

      if (userRepository.getAccountStatus(login, companyCode).equals("Blocked"))
        throw new InvalidJwtException("Your account is blocked due to multiple unsuccessful attempts. Please contact admin");
  }

  public UserDetails signUp(SignUpDto data) throws InvalidJwtException {
    final String defaultAccountStatus = "Unverified";
    loadUserByUsernameAndCompanyCode(data.login(), data.companyCode());
    loadUserByEmailIdAndCompanyCode(data.emailId(),data.companyCode());
    String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
    User newUser = new User(data.login().toLowerCase(), encryptedPassword,data.emailId(),data.title(),data.firstName(),data.middleName(),data.lastName()
            ,data.address(),data.identification(),data.mobileNo(),defaultAccountStatus, data.companyCode(), data.role());
    return userRepository.save(newUser);
  }
}