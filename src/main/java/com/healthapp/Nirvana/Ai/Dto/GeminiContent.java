package com.healthapp.Nirvana.Ai.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeminiContent {                    
    private List<GeminiPart> parts;
}
