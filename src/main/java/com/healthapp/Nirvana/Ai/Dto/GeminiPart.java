package com.healthapp.Nirvana.Ai.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //generates getter and setter
@AllArgsConstructor // generates constructors
@NoArgsConstructor //generates empty constructor
public class GeminiPart {
    private String text;
}
