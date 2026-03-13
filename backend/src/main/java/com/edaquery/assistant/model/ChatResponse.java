package com.edaquery.assistant.model;

public class ChatResponse {

  private String sessionId;
  private String answer;
  private String source;

  public ChatResponse() {
  }

  public ChatResponse(String sessionId, String answer, String source) {
    this.sessionId = sessionId;
    this.answer = answer;
    this.source = source;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }
}
