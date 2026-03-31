package com.healthapp.Nirvana.Ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthapp.Nirvana.Ai.Dto.AiAnalysisResult;
import com.healthapp.Nirvana.Ai.Dto.GeminiRequest;
import com.healthapp.Nirvana.Ai.Dto.GeminiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class AiService {

    @Value("${gemini.api.key}")         //@Value means "inject this from config file".
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();       //json <---> java object

    private static final String GEMINI_URL = "https://generativelanguage.googleapis" +
            ".com/v1beta/models/gemini-flash-latest:generateContent?key=";

    public AiAnalysisResult analyze(String journalText) {       //parses into structured json
        try {
            // Step 1 — build request body
            GeminiRequest requestBody = new GeminiRequest(journalText);

            // Step 2 — build headers
            HttpHeaders headers = new HttpHeaders();
//            headers.set("X-Goog-Api-Key", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);        //Same as setting Content-Type:application/json

            // Step 3 — combine headers + body
            HttpEntity<GeminiRequest> entity =
                    new HttpEntity<>(requestBody, headers);

            // Step 4 — make the HTTP call
            ResponseEntity<GeminiResponse> response = restTemplate.exchange(
                    GEMINI_URL + apiKey,
                    HttpMethod.POST,
                    entity,
                    GeminiResponse.class
            );
            System.out.println("Raw Gemini response: " + response.getBody());
            System.out.println("Candidates: " + response.getBody().getCandidates());

            System.out.println(response.getBody()); //error in terminal
            System.out.println("API KEY: " + apiKey);
            // Step 5 — extract text from response
            String rawText = response.getBody().getFirstText();

            //temp raw text
            System.out.println("Gemini raw text: " + rawText);
            //null check
            if (rawText == null) {
                System.out.println("Gemini responded null");
                return fallbackResult();
            }

            // Step 6 — clean the response
            String cleanText = rawText
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            // Step 7 — parse into Java object
            try {
                return objectMapper.readValue(cleanText, AiAnalysisResult.class);
            } catch (Exception parseError) {
                System.out.println("Parsing Failed: " + parseError.getMessage());
                return fallbackResult();
            }
        } catch (Exception e) {
            System.out.println("AI analysis failed: " + e.getMessage());
            return fallbackResult();
        }
    }

    // Step 8 — fallback: avoids crashes
    private AiAnalysisResult fallbackResult() {
        AiAnalysisResult fallback = new AiAnalysisResult();
        fallback.setEmotions(null);
        fallback.setSentimentScore(null);
        return fallback;
    }

    public String generateInsight(String prompt) {
        String rawText;
        try {
            GeminiRequest geminiRequest = new GeminiRequest(prompt);

            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<GeminiRequest> entity =
                    new HttpEntity<>(geminiRequest, headers);


            ResponseEntity<GeminiResponse> response = restTemplate.exchange(
                    GEMINI_URL + apiKey,
                    HttpMethod.POST,
                    entity,
                    GeminiResponse.class
            );

            rawText = response.getBody().getFirstText();
            if (rawText == null) return "Keep tracking your mood and journaling — insights will appear soon!";

        return rawText.trim();

        } catch (Exception e) {
            System.out.println("Insight generation failed: " + e.getMessage());
            return "Keep tracking your mood and journaling — insights will appear soon!";
        }
    }
}


