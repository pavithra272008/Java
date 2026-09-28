package com.example.demo.repository;

import com.example.demo.model.JournalEntry;
import com.example.demo.model.JournalType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {
    List<JournalEntry> findByJournalType(JournalType journalType);
    List<JournalEntry> findAllByOrderByEntryDateDesc();
}
