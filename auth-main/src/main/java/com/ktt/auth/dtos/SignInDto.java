package com.ktt.auth.dtos;

public record SignInDto(
    String login,
    String password,
    String companyCode) {
}
