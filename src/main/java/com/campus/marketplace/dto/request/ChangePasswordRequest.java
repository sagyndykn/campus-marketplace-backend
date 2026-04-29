package com.campus.marketplace.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank
    @Size(min = 6, message = "Пароль должен содержать минимум 6 символов")
    private String newPassword;
}
