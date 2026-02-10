package com.campus.marketplace.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
}