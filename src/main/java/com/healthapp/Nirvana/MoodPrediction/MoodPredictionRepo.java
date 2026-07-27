package com.healthapp.Nirvana.MoodPrediction;

import com.healthapp.Nirvana.Mood.MoodEntry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MoodPredictionRepo extends org.springframework.data.jpa.repository.JpaRepository<MoodEntry, Long> {
    List<MoodEntry> findByUser_IdOrderByLoggedAtDesc(@Param("userId") Long userId);
}


