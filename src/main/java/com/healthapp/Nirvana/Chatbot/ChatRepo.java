package com.healthapp.Nirvana.Chatbot;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatRepo extends JpaRepository<ChatEntry, Long> {
    List<ChatEntry> findByUserIdOrderBySentAtAsc(Long userId);

    List<ChatEntry> findTop10ByUserIdOrderBySentAtDesc(Long userId);
}
