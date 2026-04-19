package com.campus.marketplace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PresenceResponse {
    private String userId;
    private boolean online;
    private LocalDateTime lastSeenAt;
}
