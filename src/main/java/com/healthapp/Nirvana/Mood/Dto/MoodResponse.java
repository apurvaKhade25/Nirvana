package com.healthapp.Nirvana.Mood.Dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MoodResponse {

    private Long id;
    private Integer moodScore;
    private String moodLabel;
    private String note;
    private LocalDateTime loggedAt;
}
