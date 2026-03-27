package com.healthapp.Nirvana.Ai.Dto;

import lombok.Data;

import java.util.List;

//this file opens gemini response one by one like opening boxes inside boxes
@Data
public class GeminiResponse {
    private List<Candidate> candidates;

    @Data
    private static class Candidate {
        private InnerContent content;
    }

    @Data
    public static class InnerContent {
        private List<GeminiPart> parts;
    }

    public String getFirstText() {
        if (candidates != null && !candidates.isEmpty()) {
            InnerContent content = candidates.get(0).getContent();
            if (content != null && content.getParts() != null && content.getParts() != null) {
                return content.getParts().get(0).getText();
            }
        }
        return null;
    }


}
