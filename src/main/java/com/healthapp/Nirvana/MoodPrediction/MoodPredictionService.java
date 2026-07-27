package com.healthapp.Nirvana.MoodPrediction;

import com.healthapp.Nirvana.Mood.MoodEntry;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MoodPredictionService {

    private final MoodPredictionRepo moodPredictionRepo;
    private static final int MIN_FOR_LOW_CONFIDENCE = 7;
    private static final int MIN_FOR_MODERATE_CONFIDENCE = 14;
    private static final int MIN_FOR_HIGH_CONFIDENCE = 30;
    private static final double DECAY_FACTOR = 0.7;

    public PatientCycleDto analyzeMood(Long patientId) {
        List<MoodEntry> moodEntries = moodPredictionRepo.findByUser_IdOrderByLoggedAtDesc(patientId);
        // Implementation for analyzing mood entries and determining the confidence level
        ConfidenceLevel confidenceLevel = determineConfidenceLevel(moodEntries.size());

        if (confidenceLevel == ConfidenceLevel.INSUFFICIENT_DATA) {
            return new PatientCycleDto(Collections.emptyMap(), null, null, null, confidenceLevel);
        }
        Double tomorrowEstimate = computeWeightedForecast(moodEntries);
        Map<DayOfWeek, Double> weeklyPattern = calculateWeeklyPattern(moodEntries);
        DayOfWeek lowest = null;
        DayOfWeek highest = null;
        if (!weeklyPattern.isEmpty()) {
            lowest = Collections.min(weeklyPattern.entrySet(), Map.Entry.comparingByValue()).getKey();
            highest = Collections.max(weeklyPattern.entrySet(), Map.Entry.comparingByValue()).getKey();
        }

        return new PatientCycleDto(weeklyPattern, lowest, highest, tomorrowEstimate, confidenceLevel);
    }

    public ConfidenceLevel determineConfidenceLevel(int entryCount) {
        if (entryCount < MIN_FOR_LOW_CONFIDENCE) {
            return ConfidenceLevel.INSUFFICIENT_DATA;
        } else if (entryCount < MIN_FOR_MODERATE_CONFIDENCE) {
            return ConfidenceLevel.LOW;
        } else if (entryCount < MIN_FOR_HIGH_CONFIDENCE) {
            return ConfidenceLevel.MODERATE;
        } else {
            return ConfidenceLevel.HIGH;
        }
    }

    private Map<DayOfWeek, Double> calculateWeeklyPattern(List<MoodEntry> moodEntries) {
        Map<DayOfWeek, List<Double>> buckets = new EnumMap<>(DayOfWeek.class);
        for (MoodEntry entry : moodEntries) {
            DayOfWeek day = entry.getLoggedAt().getDayOfWeek();
            buckets.computeIfAbsent(day, k -> new ArrayList<>()).add((double) entry.getMoodScore());
        }
        return buckets.entrySet().stream()
                .filter(e -> (e.getValue().size() >= 2))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0.0)
                ));
    }

    private Double computeWeightedForecast(List<MoodEntry> moodEntries) {
        if (moodEntries.isEmpty()) {
            return null;
        }
        int window = Math.min(moodEntries.size(), 10); // Use the last 10 entries or fewer if not available
        double weight = 0;
        double weightedSum = 0;
        double totalWeight = 0;

        for (int i = 0; i < window; i++) {
            double w = Math.pow(DECAY_FACTOR, i);
            weightedSum += moodEntries.get(i).getMoodScore() * w;
            totalWeight += w;
        }
        log.info("Computed weighted forecast for patientId: {} with weightedSum: {}, totalWeight: {}", moodEntries.get(0).getUser().getId(), weightedSum, totalWeight);
        return (double) Math.round(weightedSum / totalWeight);
    }
}
