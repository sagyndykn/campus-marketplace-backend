package com.campus.marketplace.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    private String id;

    @Indexed
    private String conversationId;

    private String senderId;
    private String senderName;
    private String senderAvatarUrl;

    private String content;

    @Builder.Default
    private java.util.List<String> mediaUrls = new java.util.ArrayList<>();

    @Builder.Default
    private boolean read = false;

    @Builder.Default
    private LocalDateTime sentAt = LocalDateTime.now();
}
