package com.campus.marketplace.controller;

import com.campus.marketplace.dto.request.StartConversationRequest;
import com.campus.marketplace.dto.response.ConversationResponse;
import com.campus.marketplace.dto.response.MessageResponse;
import com.campus.marketplace.dto.response.PresenceResponse;
import com.campus.marketplace.service.ChatService;
import com.campus.marketplace.service.MinioService;
import com.campus.marketplace.service.PresenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final PresenceService presenceService;
    private final MinioService minioService;

    @PostMapping("/conversations")
    public ConversationResponse startConversation(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody StartConversationRequest request) {
        return chatService.getOrCreateConversation(email, request);
    }

    @GetMapping("/conversations")
    public List<ConversationResponse> getConversations(@AuthenticationPrincipal String email) {
        return chatService.getMyConversations(email);
    }

    @GetMapping("/conversations/{id}/messages")
    public List<MessageResponse> getMessages(
            @AuthenticationPrincipal String email,
            @PathVariable String id) {
        return chatService.getMessages(id, email);
    }

    @GetMapping("/presence/{userId}")
    public PresenceResponse getPresence(@PathVariable String userId) {
        return presenceService.getPresence(userId);
    }

    @PostMapping("/media/upload")
    public Map<String, String> uploadMedia(
            @AuthenticationPrincipal String email,
            @RequestParam("file") MultipartFile file) {
        String url = minioService.uploadMedia(file, "chat");
        return Map.of("url", url);
    }
}
