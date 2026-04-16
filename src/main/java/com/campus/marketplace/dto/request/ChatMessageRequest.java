package com.campus.marketplace.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class ChatMessageRequest {

    @NotBlank
    private String conversationId;

    // nullable — message may contain only media
    private String content;

    private List<String> mediaUrls;
}
