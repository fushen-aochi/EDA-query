package com.edaquery.assistant.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ChatSession {

  private String sessionId;
  private String title;
  private Instant updatedAt;
  private List<ChatMessage> messages = new ArrayList<>();

  public ChatSession() {
  }

  public ChatSession(String sessionId, String title, Instant updatedAt) {
    this.sessionId = sessionId;
    this.title = title;
    this.updatedAt = updatedAt;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  public List<ChatMessage> getMessages() {
    return messages;
  }

  public void setMessages(List<ChatMessage> messages) {
    this.messages = messages;
  }
}
