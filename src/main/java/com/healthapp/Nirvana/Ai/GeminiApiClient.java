package com.healthapp.Nirvana.Ai;

import com.healthapp.Nirvana.Ai.Dto.GeminiRequest;
import com.healthapp.Nirvana.Ai.Dto.GeminiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


@Service
@Slf4j
public class GeminiApiClient {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String geminiModel;

    public final RestTemplate restTemplate = new RestTemplate();


    private String GEMINI_URL() {
        return "https://generativelanguage.googleapis.com/v1beta/models/"
                + geminiModel + ":generateContent?key=" + apiKey;
    }

    @Retryable(retryFor = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 2.0))
    public GeminiResponse callGeminiApi(String prompt) {
        log.info("Calling Gemini API with prompt: {}", prompt);
        GeminiRequest requestBody = new GeminiRequest(prompt,true);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GeminiRequest> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<GeminiResponse> response = restTemplate.exchange(
                GEMINI_URL(),
                HttpMethod.POST,
                entity,
                GeminiResponse.class
        );
        log.info("Gemini API response: {}", response.getBody());
        return response.getBody();
    }

    @Recover
    public GeminiResponse recoverFromGeminiApiFailure(HttpClientErrorException e, String prompt) {
        log.error("Geminin api failed after retries: {} (cause: {}) (class: {})", e.getMessage(), e.getCause(),
                e.getClass().getName());
        GeminiResponse fallback = new GeminiResponse();
        fallback.setCandidates(null);
        return fallback;
    }
}
