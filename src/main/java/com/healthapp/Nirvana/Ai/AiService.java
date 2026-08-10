package com.healthapp.Nirvana.Ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthapp.Nirvana.Ai.Dto.AiAnalysisResult;
import com.healthapp.Nirvana.Ai.Dto.GeminiRequest;
import com.healthapp.Nirvana.Ai.Dto.GeminiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class AiService {

    @Value("${gemini.api.key}")
    private static String geminiApiKey;

    @Value("${gemini.model}")
    private static String geminiModel;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GeminiApiClient geminiApiClient;  // ← Inject the retry client


    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/"
            + geminiModel + ":generateContent?key=" + geminiApiKey;


    // Constructor injection
    public AiService(GeminiApiClient geminiApiClient) {
        this.geminiApiClient = geminiApiClient;
    }

    public AiAnalysisResult analyze(String journalText) {
        try {
            // Step 1 — build request body
            GeminiRequest requestBody = new GeminiRequest(journalText);

            // Step 2 — build headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Step 3 — combine headers + body
            HttpEntity<GeminiRequest> entity = new HttpEntity<>(requestBody, headers);

            // Step 4 — make the HTTP call
            ResponseEntity<GeminiResponse> response = restTemplate.exchange(
                    GEMINI_URL + geminiApiKey,
                    HttpMethod.POST,
                    entity,
                    GeminiResponse.class
            );

            // Step 5 — extract text from response
            String rawText = response.getBody().getFirstText();

            if (rawText == null) {
                log.warn("Gemini returned null response for journal analysis");
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
                log.error("Failed to parse Gemini response: {}", parseError.getMessage());
                return fallbackResult();
            }

        } catch (Exception e) {
            log.error("AI analysis failed: {}", e.getMessage());
            return fallbackResult();
        }
    }

    public String generateInsight(String prompt) {
        try {
            // ← Calls through injected bean (proxy intercepts, retry works)
            GeminiResponse response = geminiApiClient.callGeminiApi(prompt);

            String rawText = response.getFirstText();

            if (rawText == null) {
                log.warn("Gemini returned null insight response");
                return getInsightFallback();
            }

            return rawText.trim();

        } catch (Exception e) {
            log.error("Unexpected error generating insight: {} (class: {})", e.getMessage(), e.getClass().getName());
            return getInsightFallback();
        }
    }


    private AiAnalysisResult fallbackResult() {
        AiAnalysisResult fallback = new AiAnalysisResult();
        fallback.setEmotions(null);
        fallback.setSentimentScore(null);
        return fallback;
    }

    private String getInsightFallback() {
        return "Keep tracking your mood and journal entries — insights appear as patterns emerge.";
    }
}