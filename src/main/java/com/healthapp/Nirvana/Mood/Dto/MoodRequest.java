package com.healthapp.Nirvana.Mood.Dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data

public class MoodRequest {
    @NotNull(message = "Mood cannot be empty")
    @Min(value = 1, message = "Minimum score is 1")
    @Max(value = 10, message = "Maximum score is 10")

    private Integer MoodScore;
    private String Note;
    private LocalDateTime LoggedAt;
}


