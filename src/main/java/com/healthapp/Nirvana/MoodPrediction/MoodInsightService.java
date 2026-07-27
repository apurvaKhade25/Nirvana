package com.healthapp.Nirvana.MoodPrediction;

import com.healthapp.Nirvana.Ai.AiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class MoodInsightService {

    private final MoodPredictionService moodPredictionService;
    private final AiService aiService;

    private static final String INSIGHT_PROMPT_TEMPLATE = """
            You are generating a short supportive insight for a mental wellness app.
            
            Here is the patient's mood data (already computed, do not recalculate):
            - Weekly pattern by day: %s
            - Lowest-mood day: %s
            - Highest-mood day: %s
            - Tomorrow's estimated mood trend: %s
            - Confidence level: %s
            
            Rules:
            - Write 1-2 sentences only.
            - Use ONLY the numbers/days provided above. Do not invent statistics,
              percentages, or clinical claims.
            - Do not mention specific mental health diagnoses.
            - If confidence is INSUFFICIENT_DATA, do not speculate about patterns —
              instead gently encourage continued logging.
            - Tone: warm, supportive, non-alarmist. Never use language implying
              certainty about the future.
            - Do not suggest medication, treatment, or professional diagnosis.
            """;

    public MoodInsightService(MoodPredictionService moodPredictionService, AiService aiService) {

        this.moodPredictionService = moodPredictionService;
        this.aiService = aiService;
    }


    public PatientCycleInsightDto getMoodInsights(Long patientId) {
        PatientCycleDto data = moodPredictionService.analyzeMood(patientId);

        if (data.confidence() == ConfidenceLevel.INSUFFICIENT_DATA) {
            log.info("Insufficient data for patientId: {}", patientId);
            return new PatientCycleInsightDto(data, "We don't have enough data yet to provide insights. Keep logging your " +
                    "mood to help us understand your patterns.");
        }

        String prompt = String.format(INSIGHT_PROMPT_TEMPLATE,
                data.weeklyPattern(),
                data.lowestDay(),
                data.highestDay(),
                data.tomorrowEstimate(),
                data.confidence());

        String insight = aiService.generateInsight(prompt);
        log.info("Generated insight for patientId {}: {}", patientId, insight);

        return new PatientCycleInsightDto(data, insight);
    }

}

