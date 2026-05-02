package com.campus.marketplace.service.impl;

import com.campus.marketplace.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    @Async
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

    @Override
    @Async
    public void sendResetOtp(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Campus Marketplace — сброс пароля");
        message.setText(
                "Вы запросили сброс пароля.\n\n" +
                        "Ваш код подтверждения: " + otp + "\n\n" +
                        "Код действителен 5 минут.\n" +
                        "Если вы не запрашивали сброс пароля — проигнорируйте это письмо."
        );
        mailSender.send(message);
    }

    @Override
    @Async
    public void sendNewMessage(String to, String senderName, String preview) {
        String shortPreview = preview.length() > 100 ? preview.substring(0, 100) + "..." : preview;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Новое сообщение от " + senderName + " — Campus Marketplace");
        message.setText(
                senderName + " написал(а) вам:\n\n" +
                        "\"" + shortPreview + "\"\n\n" +
                        "Откройте приложение Campus Marketplace, чтобы ответить.\n\n" +
                        "— Campus Marketplace"
        );
        mailSender.send(message);
    }
}
