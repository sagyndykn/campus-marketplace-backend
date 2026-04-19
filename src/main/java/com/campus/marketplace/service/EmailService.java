package com.campus.marketplace.service;

public interface EmailService {
    void sendOtp(String to, String otp);
    void sendNewMessage(String to, String senderName, String preview);
}