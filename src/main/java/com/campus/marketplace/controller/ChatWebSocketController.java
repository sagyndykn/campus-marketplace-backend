package com.campus.marketplace.controller;

import com.campus.marketplace.dto.request.ChatMessageRequest;
import com.campus.marketplace.dto.response.MessageResponse;
import com.campus.marketplace.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(Principal principal, @Payload @Valid ChatMessageRequest request) {
        MessageResponse msg = chatService.sendMessage(principal.getName(), request);
        messagingTemplate.convertAndSend(
                "/topic/conversation/" + request.getConversationId(), msg);
        chatService.notifyRecipientIfOffline(
                request.getConversationId(),
                principal.getName(),
                msg.getSenderName(),
                msg.getContent());
    }
}
