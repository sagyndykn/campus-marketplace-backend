package com.campus.marketplace.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@sdu\\.edu\\.kz$", message = "Email должен оканчиваться на @sdu.edu.kz")
    private String email;

    @NotBlank
    private String otp;

    @NotBlank
    @Size(min = 6, message = "Пароль должен содержать минимум 6 символов")
    private String newPassword;
}
