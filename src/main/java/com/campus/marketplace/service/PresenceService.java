package com.campus.marketplace.service;

import com.campus.marketplace.dto.response.PresenceResponse;
import com.campus.marketplace.model.User;
import com.campus.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class PresenceService {

    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private final Set<String> onlineEmails = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public void connect(String email) {
        onlineEmails.add(email);
        userRepository.findByEmail(email).ifPresent(user ->
                broadcast(user.getId(), true, user.getLastSeenAt()));
    }

    public void disconnect(String email) {
        onlineEmails.remove(email);
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setLastSeenAt(LocalDateTime.now());
            userRepository.save(user);
            broadcast(user.getId(), false, user.getLastSeenAt());
        });
    }

    public PresenceResponse getPresence(String userId) {
        return userRepository.findById(userId).map(user -> {
            boolean online = onlineEmails.contains(user.getEmail());
            return new PresenceResponse(userId, online, user.getLastSeenAt());
        }).orElse(new PresenceResponse(userId, false, null));
    }

    private void broadcast(String userId, boolean online, LocalDateTime lastSeenAt) {
        messagingTemplate.convertAndSend(
                "/topic/presence/" + userId,
                new PresenceResponse(userId, online, lastSeenAt));
    }
}
