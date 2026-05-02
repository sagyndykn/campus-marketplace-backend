package com.campus.marketplace.service;

import com.campus.marketplace.dto.request.ChangePasswordRequest;
import com.campus.marketplace.dto.request.ForgotPasswordRequest;
import com.campus.marketplace.dto.request.LoginRequest;
import com.campus.marketplace.dto.request.RegisterRequest;
import com.campus.marketplace.dto.request.ResendOtpRequest;
import com.campus.marketplace.dto.request.ResetPasswordRequest;
import com.campus.marketplace.dto.request.VerifyOtpRequest;
import com.campus.marketplace.dto.response.AuthResponse;

public interface AuthService {
    void register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse verifyOtp(VerifyOtpRequest request);
    void resendOtp(ResendOtpRequest request);
    void logout(String accessToken, String refreshToken);
    AuthResponse refresh(String refreshToken);

    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);

    AuthResponse verifyResetOtp(VerifyOtpRequest request);
    void changePassword(String email, ChangePasswordRequest request);
}
