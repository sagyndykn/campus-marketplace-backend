package com.campus.marketplace.service.impl;

import com.campus.marketplace.dto.request.ChatMessageRequest;
import com.campus.marketplace.dto.request.StartConversationRequest;
import com.campus.marketplace.dto.response.ConversationResponse;
import com.campus.marketplace.dto.response.MessageResponse;
import com.campus.marketplace.model.Conversation;
import com.campus.marketplace.model.Listing;
import com.campus.marketplace.model.Message;
import com.campus.marketplace.model.User;
import com.campus.marketplace.repository.ConversationRepository;
import com.campus.marketplace.repository.ListingRepository;
import com.campus.marketplace.repository.MessageRepository;
import com.campus.marketplace.repository.UserRepository;
import com.campus.marketplace.service.ChatService;
import com.campus.marketplace.service.EmailService;
import com.campus.marketplace.service.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final PresenceService presenceService;
    private final EmailService emailService;

    @Override
    public ConversationResponse getOrCreateConversation(String requesterEmail, StartConversationRequest request) {
        User requester = getUser(requesterEmail);

        if (requester.getId().equals(request.getSellerId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нельзя начать чат с самим собой");
        }

        User other = userRepository.findById(request.getSellerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));

        Map<String, Conversation.ParticipantInfo> participants = new HashMap<>();
        participants.put(requester.getId(), Conversation.ParticipantInfo.builder()
                .name(displayName(requester))
                .avatarUrl(requester.getAvatarUrl())
                .build());
        participants.put(other.getId(), Conversation.ParticipantInfo.builder()
                .name(displayName(other))
                .avatarUrl(other.getAvatarUrl())
                .build());

        boolean isDirect = request.getListingId() == null || request.getListingId().isBlank();

        Conversation conversation;
        if (isDirect) {
            conversation = conversationRepository
                    .findDirectConversation(requester.getId(), other.getId())
                    .orElseGet(() -> conversationRepository.save(Conversation.builder()
                            .participantIds(List.of(requester.getId(), other.getId()))
                            .participants(participants)
                            .createdAt(LocalDateTime.now())
                            .build()));
        } else {
            Listing listing = listingRepository.findById(request.getListingId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Объявление не найдено"));

            conversation = conversationRepository
                    .findByBothParticipantsAndListing(requester.getId(), other.getId(), listing.getId())
                    .orElseGet(() -> conversationRepository.save(Conversation.builder()
                            .participantIds(List.of(requester.getId(), other.getId()))
                            .listingId(listing.getId())
                            .listingTitle(listing.getTitle())
                            .participants(participants)
                            .createdAt(LocalDateTime.now())
                            .build()));
        }

        return toResponse(conversation, requester.getId());
    }

    @Override
    public List<ConversationResponse> getMyConversations(String email) {
        User user = getUser(email);
        return conversationRepository
                .findByParticipantIdsContainingOrderByLastMessageAtDesc(user.getId())
                .stream()
                .map(c -> toResponse(c, user.getId()))
                .toList();
    }

    @Override
    public List<MessageResponse> getMessages(String conversationId, String email) {
        User user = getUser(email);
        Conversation conversation = getConversation(conversationId);
        checkParticipant(conversation, user.getId());

        return messageRepository.findByConversationIdOrderBySentAtAsc(conversationId)
                .stream()
                .map(m -> toMessageResponse(m, user.getId()))
                .toList();
    }

    @Override
    public MessageResponse sendMessage(String senderEmail, ChatMessageRequest request) {
        User sender = getUser(senderEmail);
        Conversation conversation = getConversation(request.getConversationId());
        checkParticipant(conversation, sender.getId());

        boolean hasText = request.getContent() != null && !request.getContent().isBlank();
        boolean hasMedia = request.getMediaUrls() != null && !request.getMediaUrls().isEmpty();
        if (!hasText && !hasMedia) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Сообщение не может быть пустым");
        }

        Message message = messageRepository.save(Message.builder()
                .conversationId(conversation.getId())
                .senderId(sender.getId())
                .senderName(displayName(sender))
                .senderAvatarUrl(sender.getAvatarUrl())
                .content(hasText ? request.getContent() : null)
                .mediaUrls(hasMedia ? request.getMediaUrls() : new java.util.ArrayList<>())
                .sentAt(LocalDateTime.now())
                .build());

        String preview = hasText ? request.getContent() : "📎 Медиафайл";
        conversation.setLastMessage(preview);
        conversation.setLastMessageAt(message.getSentAt());
        conversationRepository.save(conversation);

        return toMessageResponse(message, sender.getId());
    }

    @Override
    public void notifyRecipientIfOffline(String conversationId, String senderEmail, String senderName, String content) {
        User sender = userRepository.findByEmail(senderEmail).orElse(null);
        if (sender == null) return;

        conversationRepository.findById(conversationId).ifPresent(conv ->
            conv.getParticipantIds().stream()
                .filter(id -> !id.equals(sender.getId()))
                .forEach(recipientId -> {
                    var presence = presenceService.getPresence(recipientId);
                    if (!presence.isOnline()) {
                        userRepository.findById(recipientId).ifPresent(recipient -> {
                            try {
                                emailService.sendNewMessage(recipient.getEmail(), senderName, content);
                            } catch (Exception e) {
                                log.warn("Failed to send new-message email to {}: {}", recipient.getEmail(), e.getMessage());
                            }
                        });
                    }
                })
        );
    }

    private ConversationResponse toResponse(Conversation c, String viewerId) {
        String otherId = c.getParticipantIds().stream()
                .filter(id -> !id.equals(viewerId))
                .findFirst()
                .orElse(null);

        Conversation.ParticipantInfo other = otherId != null ? c.getParticipants().get(otherId) : null;

        return ConversationResponse.builder()
                .id(c.getId())
                .otherUserId(otherId)
                .otherUserName(other != null ? other.getName() : "Пользователь")
                .otherUserAvatarUrl(other != null ? other.getAvatarUrl() : null)
                .listingId(c.getListingId())
                .listingTitle(c.getListingTitle())
                .lastMessage(c.getLastMessage())
                .lastMessageAt(c.getLastMessageAt())
                .build();
    }

    private MessageResponse toMessageResponse(Message m, String viewerId) {
        return MessageResponse.builder()
                .id(m.getId())
                .conversationId(m.getConversationId())
                .senderId(m.getSenderId())
                .senderName(m.getSenderName())
                .senderAvatarUrl(m.getSenderAvatarUrl())
                .content(m.getContent())
                .mediaUrls(m.getMediaUrls())
                .sentAt(m.getSentAt())
                .mine(m.getSenderId().equals(viewerId))
                .build();
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    private Conversation getConversation(String id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Чат не найден"));
    }

    private void checkParticipant(Conversation c, String userId) {
        if (!c.getParticipantIds().contains(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет доступа к этому чату");
        }
    }

    private String displayName(User u) {
        String name = ((u.getFirstName() != null ? u.getFirstName() : "") + " " +
                (u.getLastName() != null ? u.getLastName() : "")).trim();
        return name.isEmpty() ? u.getEmail() : name;
    }
}
