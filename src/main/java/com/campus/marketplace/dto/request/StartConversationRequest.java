package com.campus.marketplace.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StartConversationRequest {

    @NotBlank
    private String sellerId;

    // null = direct user-to-user chat (no listing)
    private String listingId;
}
