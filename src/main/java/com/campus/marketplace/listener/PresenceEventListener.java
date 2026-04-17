package com.campus.marketplace.listener;

import com.campus.marketplace.service.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class PresenceEventListener {

    private final PresenceService presenceService;

    @EventListener
    public void onConnect(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        if (accessor.getUser() != null) {
            presenceService.connect(accessor.getUser().getName());
        }
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        if (accessor.getUser() != null) {
            presenceService.disconnect(accessor.getUser().getName());
        }
    }
}
