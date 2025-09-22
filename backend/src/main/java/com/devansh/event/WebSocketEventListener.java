package com.devansh.event;

import com.devansh.engine.CrdtManagerService;
import com.devansh.model.User;
import com.devansh.model.WebSocketSession;
import com.devansh.repo.UserRepository;
import com.devansh.response.ActiveUsers;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketEventListener {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    @Autowired
    private CrdtManagerService crdtManagerService;
    @Autowired
    private UserRepository userRepository;

    private ConcurrentHashMap<String, WebSocketSession> socketSession;
    private ConcurrentHashMap<String, List<String>> docSession;

    public WebSocketEventListener() {
        socketSession = new ConcurrentHashMap<>();
        docSession = new ConcurrentHashMap<>();
    }

    @EventListener
    private void handleSessionConnected(SessionConnectedEvent event) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String sessionId = getSessionId(headerAccessor);
        String displayName = getDisplayName(headerAccessor);
        socketSession.put(sessionId, new WebSocketSession(displayName, ""));
    }

    @EventListener
    private void handleSessionSubscribe(SessionSubscribeEvent event) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String docId = getDocId(headerAccessor);
        if (docId == "") return;
        String sessionId = getSessionId(headerAccessor);
        socketSession.get(sessionId).setDocId(docId);
        if (docSession.containsKey(docId) == false) {
            crdtManagerService.createCrdt(Long.parseLong(docId));
        }
        List<String> docSessionParticipants = docSession.getOrDefault(docId, new ArrayList<>());
        docSessionParticipants.add(sessionId);
        docSession.put(docId, docSessionParticipants);
        notifyActiveUsers(docId);
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String sessionId = getSessionId(headerAccessor);
        WebSocketSession sessionData = socketSession.get(sessionId);
        socketSession.remove(sessionId);
        if (sessionData == null) return;
        String docId = sessionData.getDocId();
        List<String> docSessionParticipants = docSession.get(docId);
        if (docSessionParticipants == null) return;
        docSessionParticipants.remove(sessionId);
        if (docSessionParticipants.size() == 0) {
            docSession.remove(docId);
            crdtManagerService.saveAndDeleteCrdt(Long.parseLong(docId));
        }
        notifyActiveUsers(docId);
    }


    private String getSessionId(SimpMessageHeaderAccessor headerAccessor) {
        return headerAccessor.getSessionId();
    }

    private String getDisplayName(SimpMessageHeaderAccessor headerAccessor) {
        String email = headerAccessor.getUser().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return user.getDisplayName();
    }

    private String getDocId(SimpMessageHeaderAccessor headerAccessor) {
        return extractDocIdFromPath(headerAccessor.getDestination());
    }

    private String extractDocIdFromPath(String path) {
        UriTemplate uriTemplate = new UriTemplate("/docs/broadcast/changes/{id}");
        Map<String, String> matchResult = uriTemplate.match(path);
        return matchResult.getOrDefault("id", "");
    }

    private void notifyActiveUsers(String docId) {
        List<String> docSessionPaticipants = docSession.get(docId);
        if (docSessionPaticipants == null) {
            return;
        }
        ActiveUsers activeUsers = new ActiveUsers();
        List<String> displayNames = docSessionPaticipants.stream()
                .map((sessionKey) -> socketSession.get(sessionKey).getDisplayName())
                .toList();
        activeUsers.setDisplayNames(displayNames);
        messagingTemplate.convertAndSend("/docs/broadcast/usernames/" + docId, activeUsers);
    }


}
























