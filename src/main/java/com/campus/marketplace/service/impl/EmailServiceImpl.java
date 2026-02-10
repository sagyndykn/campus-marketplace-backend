package com.campus.marketplace.service.impl;

import com.campus.marketplace.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendOtp(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Campus Marketplace — ваш код входа");
        message.setText(
                "Ваш код подтверждения: " + otp + "\n\n" +
                        "Код действителен 2 минуты.\n" +
                        "Если вы не запрашивали код — проигнорируйте это письмо."
        );
        mailSender.send(message);
    }
}
