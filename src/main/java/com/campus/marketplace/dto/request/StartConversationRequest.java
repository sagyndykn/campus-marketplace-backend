package com.campus.marketplace.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StartConversationRequest {

    @NotBlank
    private String sellerId;

    private String listingId;
}
