package com.healthapp.Nirvana.Mood.Dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MoodResponse {

    private Long id;
    private Integer MoodScore;
    private String MoodLabel;
    private String Note;
    private LocalDateTime loggedAt;
}
