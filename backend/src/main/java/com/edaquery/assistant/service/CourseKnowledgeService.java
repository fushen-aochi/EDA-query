package com.edaquery.assistant.service;

import com.edaquery.assistant.model.KnowledgeEntry;
import com.edaquery.assistant.repository.KnowledgeEntryRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CourseKnowledgeService {

  private final KnowledgeEntryRepository knowledgeEntryRepository;

  public CourseKnowledgeService(KnowledgeEntryRepository knowledgeEntryRepository) {
    this.knowledgeEntryRepository = knowledgeEntryRepository;
  }

  public Optional<KnowledgeEntry> findBestMatch(String question) {
    List<KnowledgeEntry> candidates = knowledgeEntryRepository
        .findTop5ByQuestionContainingIgnoreCaseOrKeywordsContainingIgnoreCase(question, question);

    return candidates.stream()
        .max(Comparator.comparingInt(entry -> score(question, entry)));
  }

  private int score(String input, KnowledgeEntry entry) {
    int score = 0;
    String normalizedInput = input.toLowerCase();

    if (entry.getQuestion() != null && entry.getQuestion().toLowerCase().contains(normalizedInput)) {
      score += 5;
    }

    if (entry.getKeywords() != null) {
      String[] keywordTokens = entry.getKeywords().toLowerCase().split("[,，\\s]+");
      for (String token : keywordTokens) {
        if (!token.isBlank() && normalizedInput.contains(token)) {
          score += 2;
        }
      }
    }

    return score;
  }
}
