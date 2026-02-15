package com.campus.marketplace.service;

import com.campus.marketplace.dto.request.LoginRequest;
import com.campus.marketplace.dto.request.RegisterRequest;
import com.campus.marketplace.dto.request.VerifyOtpRequest;
import com.campus.marketplace.dto.response.AuthResponse;

public interface AuthService {
    void register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse verifyOtp(VerifyOtpRequest request);
}