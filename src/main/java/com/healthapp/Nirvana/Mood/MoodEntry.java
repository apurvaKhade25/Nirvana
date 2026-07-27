package com.healthapp.Nirvana.Mood;

import com.healthapp.Nirvana.User.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity()
@Table(name = "mood_entries")
@Data
public class MoodEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)       //awful=1 great=5
    private Integer moodScore;

    private String moodLabel;      // "awful","bad","okay","good","great"

    @Column(columnDefinition = "Text")
    private  String note;

    private LocalDateTime loggedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void Oncreate(){
        createdAt=LocalDateTime.now();
        if (loggedAt==null){
            loggedAt=createdAt;
        }
    }
}
