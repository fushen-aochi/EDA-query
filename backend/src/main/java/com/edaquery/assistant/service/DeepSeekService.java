package com.edaquery.assistant.service;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class DeepSeekService {

  private final WebClient webClient;

  @Value("${app.deepseek.api-key:}")
  private String apiKey;

  @Value("${app.deepseek.model:deepseek-chat}")
  private String model;

  public DeepSeekService(@Value("${app.deepseek.base-url:https://api.deepseek.com}") String baseUrl) {
    this.webClient = WebClient.builder().baseUrl(baseUrl).build();
  }

  public String ask(String userQuestion) {
    if (apiKey == null || apiKey.isBlank()) {
      return "未配置 DeepSeek API Key。请在 application.yml 的 app.deepseek.api-key 或环境变量中填写后重试。";
    }

    Map<String, Object> body = Map.of(
        "model", model,
        "messages", List.of(
            Map.of("role", "system", "content", "你是EDA课程助教，回答应准确简洁。"),
            Map.of("role", "user", "content", userQuestion)));

    DeepSeekResponse response;
    try {
      response = webClient.post()
          .uri("/chat/completions")
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(body)
          .retrieve()
          .bodyToMono(DeepSeekResponse.class)
          .block();
    } catch (WebClientResponseException ex) {
      if (ex.getStatusCode().value() == 401) {
        return "DeepSeek 认证失败（401）。请检查 API Key 是否可用、是否过期，或是否有该模型调用权限。";
      }
      return "DeepSeek 调用失败（HTTP " + ex.getStatusCode().value() + "）。请稍后重试。";
    } catch (Exception ex) {
      return "DeepSeek 服务暂时不可用，请稍后重试。";
    }

    if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
      return "DeepSeek 返回为空，请稍后重试。";
    }

    DeepSeekResponse.Choice first = response.getChoices().get(0);
    if (first.getMessage() == null || first.getMessage().getContent() == null
        || first.getMessage().getContent().isBlank()) {
      return "DeepSeek 未返回有效答案，请稍后重试。";
    }

    return first.getMessage().getContent().trim();
  }

  public static class DeepSeekResponse {
    private List<Choice> choices;

    public List<Choice> getChoices() {
      return choices;
    }

    public void setChoices(List<Choice> choices) {
      this.choices = choices;
    }

    public static class Choice {
      private Message message;

      public Message getMessage() {
        return message;
      }

      public void setMessage(Message message) {
        this.message = message;
      }
    }

    public static class Message {
      private String role;
      private String content;

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
    }
  }
}
