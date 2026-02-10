package com.campus.marketplace.service.impl;

import com.campus.marketplace.service.EmailService;
import com.campus.marketplace.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final StringRedisTemplate redisTemplate;
    private final EmailService emailService;

    private static final String OTP_PREFIX = "otp:";
    private static final long OTP_TTL = 2;

    @Override
    public void generateAndSend(String email) {
        String otp = generateOtp();
        saveToRedis(email, otp);
        emailService.sendOtp(email, otp);
    }

    @Override
    public boolean verify(String email, String otp) {
        String key = OTP_PREFIX + email;
        String saved = redisTemplate.opsForValue().get(key);

        if (saved != null && saved.equals(otp)) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    private void saveToRedis(String email, String otp) {
        redisTemplate.opsForValue().set(OTP_PREFIX + email, otp, OTP_TTL, TimeUnit.MINUTES);
    }
}