package com.edaquery.assistant.controller;

import com.edaquery.assistant.model.ChatMessage;
import com.edaquery.assistant.model.ChatRequest;
import com.edaquery.assistant.model.ChatResponse;
import com.edaquery.assistant.model.ChatSession;
import com.edaquery.assistant.service.ChatService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

  private final ChatService chatService;

  public ChatController(ChatService chatService) {
    this.chatService = chatService;
  }

  @PostMapping("/send")
  public ChatResponse send(@Valid @RequestBody ChatRequest request) {
    return chatService.answer(request.getSessionId(), request.getMessage());
  }

  @GetMapping("/sessions")
  public List<ChatSession> sessions() {
    return chatService.listSessions();
  }

  @GetMapping("/sessions/{sessionId}/messages")
  public List<ChatMessage> messages(@PathVariable String sessionId) {
    return chatService.listMessages(sessionId);
  }
}
