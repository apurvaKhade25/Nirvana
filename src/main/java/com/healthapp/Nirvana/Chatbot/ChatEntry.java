package com.healthapp.Nirvana.Chatbot;

import com.healthapp.Nirvana.User.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Locale;

@Entity()
@Table(name = "chat_entries")
@Data
public class ChatEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Column(columnDefinition = "Text")
    private String message;

    @Column(nullable = false,updatable = false)
    private LocalDateTime sentAt;

    private String role;
}
