package com.campus.marketplace.service;

import com.campus.marketplace.dto.request.ChatMessageRequest;
import com.campus.marketplace.dto.request.StartConversationRequest;
import com.campus.marketplace.dto.response.ConversationResponse;
import com.campus.marketplace.dto.response.MessageResponse;

import java.util.List;

public interface ChatService {

    ConversationResponse getOrCreateConversation(String requesterEmail, StartConversationRequest request);

    List<ConversationResponse> getMyConversations(String email);

    List<MessageResponse> getMessages(String conversationId, String email);

    MessageResponse sendMessage(String senderEmail, ChatMessageRequest request);

    void notifyRecipientIfOffline(String conversationId, String senderEmail, String senderName, String content);
}
