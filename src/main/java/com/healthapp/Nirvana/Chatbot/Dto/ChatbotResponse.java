package com.healthapp.Nirvana.Chatbot.Dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatbotResponse {
    private String message;
    private LocalDateTime sentAt;
}
