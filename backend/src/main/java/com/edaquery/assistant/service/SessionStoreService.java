package com.edaquery.assistant.service;

import com.edaquery.assistant.model.ChatMessage;
import com.edaquery.assistant.model.ChatSession;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class SessionStoreService {

  private final Map<String, ChatSession> sessions = new ConcurrentHashMap<>();

  public ChatSession getOrCreateSession(String sessionId, String firstQuestion) {
    String realId = (sessionId == null || sessionId.isBlank()) ? UUID.randomUUID().toString() : sessionId;
    return sessions.computeIfAbsent(realId, id -> new ChatSession(id, buildTitle(firstQuestion), Instant.now()));
  }

  public void appendMessage(String sessionId, ChatMessage message) {
    ChatSession session = sessions.get(sessionId);
    if (session == null) {
      return;
    }
    session.getMessages().add(message);
    session.setUpdatedAt(Instant.now());
  }

  public List<ChatSession> listSessions() {
    List<ChatSession> result = new ArrayList<>(sessions.values());
    result.sort(Comparator.comparing(ChatSession::getUpdatedAt).reversed());
    return result;
  }

  public List<ChatMessage> listMessages(String sessionId) {
    ChatSession session = sessions.get(sessionId);
    return session == null ? List.of() : session.getMessages();
  }

  private String buildTitle(String firstQuestion) {
    if (firstQuestion == null || firstQuestion.isBlank()) {
      return "新会话";
    }
    String trimmed = firstQuestion.trim();
    return trimmed.length() > 16 ? trimmed.substring(0, 16) + "..." : trimmed;
  }
}
