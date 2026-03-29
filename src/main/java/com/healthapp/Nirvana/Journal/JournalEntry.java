package com.healthapp.Nirvana.Journal;

import com.healthapp.Nirvana.User.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name ="journal_entries")
@Data
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "Text")
    private String content;

    private LocalDateTime loggedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
