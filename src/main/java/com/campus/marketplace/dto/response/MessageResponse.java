package com.campus.marketplace.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class MessageResponse {

    private String id;
    private String conversationId;
    private String senderId;
    private String senderName;
    private String senderAvatarUrl;
    private String content;
    private List<String> mediaUrls;
    private LocalDateTime sentAt;
    private boolean mine;
}
