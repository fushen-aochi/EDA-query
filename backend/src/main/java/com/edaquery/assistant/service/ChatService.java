package com.edaquery.assistant.service;

import com.edaquery.assistant.model.ChatMessage;
import com.edaquery.assistant.model.ChatResponse;
import com.edaquery.assistant.model.ChatSession;
import com.edaquery.assistant.model.KnowledgeEntry;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

  private final CourseKnowledgeService courseKnowledgeService;
  private final DeepSeekService deepSeekService;
  private final SessionStoreService sessionStoreService;

  public ChatService(CourseKnowledgeService courseKnowledgeService,
      DeepSeekService deepSeekService,
      SessionStoreService sessionStoreService) {
    this.courseKnowledgeService = courseKnowledgeService;
    this.deepSeekService = deepSeekService;
    this.sessionStoreService = sessionStoreService;
  }

  public ChatResponse answer(String sessionId, String message) {
    ChatSession session = sessionStoreService.getOrCreateSession(sessionId, message);
    sessionStoreService.appendMessage(session.getSessionId(),
        new ChatMessage("user", message, "user", Instant.now()));

    Optional<KnowledgeEntry> localAnswer = courseKnowledgeService.findBestMatch(message);
    String assistantAnswer;
    String source;

    if (localAnswer.isPresent()) {
      assistantAnswer = localAnswer.get().getAnswer();
      source = "teacher-sql";
    } else {
      assistantAnswer = deepSeekService.ask(message);
      source = "deepseek";
    }

    sessionStoreService.appendMessage(session.getSessionId(),
        new ChatMessage("assistant", assistantAnswer, source, Instant.now()));

    return new ChatResponse(session.getSessionId(), assistantAnswer, source);
  }

  public List<ChatSession> listSessions() {
    return sessionStoreService.listSessions();
  }

  public List<ChatMessage> listMessages(String sessionId) {
    return sessionStoreService.listMessages(sessionId);
  }
}
