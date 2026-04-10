package com.campus.marketplace.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@sdu\\.edu\\.kz$", message = "Email должен оканчиваться на @sdu.edu.kz")
    private String email;

    @NotBlank
    @Size(min = 6, max = 100, message = "Пароль должен содержать от 6 до 100 символов")
    private String password;
}