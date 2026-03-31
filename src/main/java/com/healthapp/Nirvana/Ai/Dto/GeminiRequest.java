package com.healthapp.Nirvana.Ai.Dto;

import lombok.Data;

import java.util.List;

@Data
public class GeminiRequest {
    private List<GeminiContent> contents;

    public GeminiRequest(String journalText) {
        // existing constructor — adds journal analysis prompt
        String prompt = """
            Analyze the emotional content of this journal entry.
            ...
            Journal entry: "%s"
            """.formatted(journalText);

        GeminiPart part = new GeminiPart(prompt);
        GeminiContent content = new GeminiContent(List.of(part));
        this.contents = List.of(content);
    }

    //for plain prompts like wellness insight
    public GeminiRequest(String text, boolean isPlainPrompt) {
        GeminiPart part = new GeminiPart(text); // use text as-is, no wrapping
        GeminiContent content = new GeminiContent(List.of(part));
        this.contents = List.of(content);
    }
}
