package com.example.userRoleAuth.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class OtpService {
    @Autowired
    private JavaMailSender javaMailSender;

    private final Map<String, String> otpCache = new ConcurrentHashMap<>();

    public void sendOtp(String email, String otp) {
        otpCache.put(email, otp);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is : " + otp);
        javaMailSender.send(message);
    }

    public String getOtp(String email) {
        return otpCache.get(email);
    }

    public void clearOtp(String email) {
        otpCache.remove(email);
    }
}
