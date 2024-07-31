package com.example.userRoleAuth.entity;

import lombok.Data;

@Data
public class OtpRequest {
    private String otpToken;
    private String otp;
}
