package com.healthapp.Nirvana.Journal.Dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JournalResponse {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
}
