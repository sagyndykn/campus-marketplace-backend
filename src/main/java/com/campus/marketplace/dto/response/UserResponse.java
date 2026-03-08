package com.campus.marketplace.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private String role;
    private boolean isVerified;
}
