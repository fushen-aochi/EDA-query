package com.edaquery.assistant.repository;

import com.edaquery.assistant.model.KnowledgeEntry;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KnowledgeEntryRepository extends JpaRepository<KnowledgeEntry, Long> {

  List<KnowledgeEntry> findTop5ByQuestionContainingIgnoreCaseOrKeywordsContainingIgnoreCase(String question,
      String keywords);
}
