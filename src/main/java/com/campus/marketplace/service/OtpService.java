package com.campus.marketplace.service;

public interface OtpService {
    void generateAndSend(String email);
    boolean verify(String email, String otp);
}