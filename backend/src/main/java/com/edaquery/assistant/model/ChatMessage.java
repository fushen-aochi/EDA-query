package com.edaquery.assistant.model;

import java.time.Instant;

public class ChatMessage {

  private String role;
  private String content;
  private String source;
  private Instant createdAt;

  public ChatMessage() {
  }

  public ChatMessage(String role, String content, String source, Instant createdAt) {
    this.role = role;
    this.content = content;
    this.source = source;
    this.createdAt = createdAt;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
