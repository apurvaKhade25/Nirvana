package com.healthapp.Nirvana.Journal.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JournalRequest {

    @NotBlank(message = "Title cannot be null")
    String title;

    @NotBlank(message = "Journal Cannot be null")
    String content;
}
