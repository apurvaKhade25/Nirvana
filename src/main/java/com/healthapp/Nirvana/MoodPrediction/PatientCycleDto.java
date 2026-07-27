package com.healthapp.Nirvana.MoodPrediction;

import java.time.DayOfWeek;
import java.util.Map;

public record PatientCycleDto(
        Map<DayOfWeek, Double> weeklyPattern,
        DayOfWeek lowestDay,
        DayOfWeek highestDay,
        Double tomorrowEstimate,
        ConfidenceLevel confidence) {
}
