package com.healthapp.Nirvana.Wellness;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthapp.Nirvana.Ai.AiService;
import com.healthapp.Nirvana.Journal.JournalEntry;
import com.healthapp.Nirvana.Journal.JournalRepo;
import com.healthapp.Nirvana.Mood.Dto.MoodResponse;
import com.healthapp.Nirvana.Mood.MoodEntry;
import com.healthapp.Nirvana.Mood.MoodRepo;
import com.healthapp.Nirvana.Wellness.Dto.EmotionDto;
import com.healthapp.Nirvana.Wellness.Dto.WellnessResponse;
import com.nimbusds.oauth2.sdk.ciba.CIBATokenDelivery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

@Service
public class WellnessService {

    private final MoodRepo moodRepo;
    private final AiService aiService;
    private final JournalRepo journalRepo;

    public WellnessService(MoodRepo moodRepo, JournalRepo journalRepo, AiService aiService) {
        this.moodRepo = moodRepo;
        this.journalRepo = journalRepo;
        this.aiService = aiService;
    }


    public WellnessResponse getReport(Long userId) {
        //fetch all moods
        List<MoodEntry> moods = moodRepo.findByUserIdOrderByLoggedAtAsc(userId);


        //calculate average moodscore
        double avg = moods.stream()                  //extracts the moodScore from each entry as a number
                .mapToInt(MoodEntry::getMoodScore)  //calculated avg
                .average().orElse(0);         //return 0 instead of crashing


        //calculates most logged moodlabel
        String mostFrequent = moods.stream()
                .collect(groupingBy(MoodEntry::getMoodLabel, counting()))     //{ "calm": 4, "anxious": 2, "happy": 3 }
                .entrySet().stream()                                         //turns map into stream of key value pairs
                .max(Map.Entry.comparingByValue())                           // label with highest count
                .map(Map.Entry::getKey)                                      //extracts just the label name e.g. "calm"
                .orElse("NA");

        //calculate mood with best score
        MoodEntry best = moods.stream()
                .max(Comparator.comparingInt(MoodEntry::getMoodScore))
                .orElse(null);

        //calculate mood with worst score
        MoodEntry worst = moods.stream()
                .min(Comparator.comparingInt(MoodEntry::getMoodScore))
                .orElse(null);


        //extracts journalentries
        List<JournalEntry> journalEntries = journalRepo.findByUserIdOrderByCreatedAtDesc(userId);
        int journalCount = journalEntries.size();         //total entries


        //build prompt for gemini
        String prompt = String.format(
                "User's wellness data: average mood %.1f/10, " +
                        "most frequent emotion: %s, total mood logs: %d, journal entries: %d. " +
                        "Return ONLY a valid JSON object with no markdown, no backticks, exactly like this: " +
                        "{" +
                        "\"insight\": \"2-3 sentence wellness summary\"," +
                        "\"emotions\": [" +
                        "  {\"name\": \"Calm\", \"score\": 0.45}," +
                        "  {\"name\": \"Joy\", \"score\": 0.30}," +
                        "  {\"name\": \"Anxious\", \"score\": 0.25}" +
                        "]," +
                        "\"recommendations\": [" +
                        "  \"recommendation 1\"," +
                        "  \"recommendation 2\"," +
                        "  \"recommendation 3\"" +
                        "]" +
                        "}",
                avg, mostFrequent, moods.size(), journalCount
        );


        WellnessResponse response = new WellnessResponse();
        response.setAvgMoodScore((double)Math.round(avg * 10) / 10);
        response.setAvgFrequentMood(mostFrequent);
        response.setTotalJournalEntries(journalCount);
        response.setTotalMoodLogs(moods.size());

        // If best/worst exist, extract just the date part from the timestamp
        response.setBestday(best!=null ? best.getLoggedAt().toLocalDate().toString():"NA" );
        response.setWorstday(worst!=null ? worst.getLoggedAt().toLocalDate().toString():"NA");


        // With this — parse structured JSON from Gemini
        String rawJson = aiService.generateInsight(prompt);
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(rawJson);

            response.setAiInsight(root.path("insight").asText());

            // parse emotions
            List<EmotionDto> emotions = new ArrayList<>();
            root.path("emotions").forEach(e -> {
                EmotionDto dto = new EmotionDto();
                dto.setName(e.path("name").asText());
                dto.setScore(e.path("score").asDouble());
                emotions.add(dto);
            });
            response.setEmotions(emotions);

            // parse recommendations
            List<String> recs = new ArrayList<>();
            root.path("recommendations").forEach(r -> recs.add(r.asText()));
            response.setRecommendations(recs);

        } catch (Exception e) {
            System.out.println("Failed to parse Gemini JSON: " + e.getMessage());
            response.setAiInsight(rawJson); // fallback to raw text
            response.setEmotions(List.of());
            response.setRecommendations(List.of());
        }

//        response.setAiInsight(aiInsight);


        return response;
    }

}
