package com.campus.marketplace.model;

import com.campus.marketplace.enums.Role;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String firstName;
    private String lastName;
    private String avatarUrl;
    private String phone;
    private String password;

    private Role role;

    private boolean isVerified;
    private boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastSeenAt;

    @Builder.Default
    private java.util.Set<String> favoriteListingIds = new java.util.HashSet<>();
}