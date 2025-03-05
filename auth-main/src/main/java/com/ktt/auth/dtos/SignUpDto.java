package com.ktt.auth.dtos;

import com.ktt.auth.enums.UserRole;

import java.sql.Blob;
import java.time.LocalDateTime;

public record SignUpDto(
    String login,
    String password,
    String emailId,
    String title,
    String firstName,
    String middleName,
    String lastName,
    String address,
    String identification,
    byte[] Photo,
    String mobileNo,
    String companyCode,
    UserRole role) {
}
