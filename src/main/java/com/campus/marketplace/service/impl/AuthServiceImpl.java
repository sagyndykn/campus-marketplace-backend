package com.campus.marketplace.service.impl;

import com.campus.marketplace.dto.request.LoginRequest;
import com.campus.marketplace.dto.request.RegisterRequest;
import com.campus.marketplace.dto.request.VerifyOtpRequest;
import com.campus.marketplace.dto.response.AuthResponse;
import com.campus.marketplace.enums.Role;
import com.campus.marketplace.model.User;
import com.campus.marketplace.repository.UserRepository;
import com.campus.marketplace.security.JwtService;
import com.campus.marketplace.service.AuthService;
import com.campus.marketplace.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final JwtService jwtService;

    @Override
    public void register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        User user = User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(Role.USER)
                .isVerified(false)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
        otpService.generateAndSend(request.getEmail());
    }

    @Override
    public void login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!user.isActive()) {
            throw new RuntimeException("Аккаунт заблокирован");
        }

        otpService.generateAndSend(request.getEmail());
    }

    @Override
    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        boolean valid = otpService.verify(request.getEmail(), request.getOtp());

        if (!valid) {
            throw new RuntimeException("Неверный или истёкший OTP код");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!user.isVerified()) {
            user.setVerified(true);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getEmail(), user.getRole().name());
    }
}