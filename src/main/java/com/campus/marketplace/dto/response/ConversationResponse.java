package com.campus.marketplace.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ConversationResponse {

    private String id;
    private String otherUserId;
    private String otherUserName;
    private String otherUserAvatarUrl;
    private String listingId;
    private String listingTitle;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
}
