package com.healthapp.Nirvana.Journal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JournalRepo extends JpaRepository<JournalEntry,Long> {

    List <JournalEntry> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional <JournalEntry> findByIdAndUserId(Long id, Long userId);

}
