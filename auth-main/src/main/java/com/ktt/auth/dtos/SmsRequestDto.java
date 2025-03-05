package com.ktt.auth.dtos;

import lombok.Getter;

@Getter
public class SmsRequestDto {
    private String mobileNo;
    private String message;
}
