package com.campus.marketplace.controller;

import com.campus.marketplace.dto.request.LoginRequest;
import com.campus.marketplace.dto.request.RefreshRequest;
import com.campus.marketplace.dto.request.RegisterRequest;
import com.campus.marketplace.dto.request.ResendOtpRequest;
import com.campus.marketplace.dto.request.VerifyOtpRequest;
import com.campus.marketplace.dto.response.AuthResponse;
import com.campus.marketplace.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("OTP отправлен на " + request.getEmail());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(authService.verifyOtp(request));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(@Valid @RequestBody ResendOtpRequest request) {
        authService.resendOtp(request);
        return ResponseEntity.ok("OTP повторно отправлен на " + request.getEmail());
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request,
                                         @RequestBody(required = false) RefreshRequest body) {
        String header = request.getHeader("Authorization");
        String accessToken = (header != null && header.startsWith("Bearer ")) ? header.substring(7) : null;
        String refreshToken = (body != null) ? body.getRefreshToken() : null;
        authService.logout(accessToken, refreshToken);
        return ResponseEntity.ok("Выход выполнен");
    }
}
