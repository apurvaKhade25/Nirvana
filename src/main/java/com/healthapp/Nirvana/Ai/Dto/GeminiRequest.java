package com.healthapp.Nirvana.Ai.Dto;

import lombok.Data;

import java.util.List;

@Data
public class GeminiRequest {
    private List<GeminiContent> contents;

    public GeminiRequest(String journalText) {
        String prompt = """
                Analyze the emotional content of this journal entry.
                Return ONLY a valid JSON object with no extra text,
                no markdown, no backticks.
                Format exactly like this:
                {
                  "emotions": "emotion1, emotion2, emotion3",
                  "sentimentScore": 3.5
                }
                
                Rules:
                - emotions: exactly 3 comma separated emotion words
                - sentimentScore: number between 1.0 (very negative)
                  and 5.0 (very positive)
                
                Journal entry: "%s"
                """.formatted(journalText);

        GeminiPart part = new GeminiPart(prompt);  //wrap in geminipart: puts prompt in to part object
        GeminiContent content = new GeminiContent(List.of(part));   // puts part into content object
        this.contents = List.of(content);      //Sets the final contents list on the request.

    }
}
