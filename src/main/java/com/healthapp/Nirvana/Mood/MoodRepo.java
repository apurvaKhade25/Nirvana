package com.healthapp.Nirvana.Mood;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MoodRepo extends JpaRepository<MoodEntry,Long> {

    List <MoodEntry> findByUserIdOrderByLoggedAtDesc(Long UserId);

    List <MoodEntry> findByUserIdAndLoggedAtBetweenOrderByLoggedAtAsc(
            Long UserId, LocalDateTime from, LocalDateTime to
    );


}
