package com.healthapp.Nirvana.Mood;


import com.healthapp.Nirvana.Exception.ResourceNotFoundException;
import com.healthapp.Nirvana.Mood.Dto.MoodRequest;
import com.healthapp.Nirvana.Mood.Dto.MoodResponse;
import com.healthapp.Nirvana.User.User;
import com.healthapp.Nirvana.User.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MoodService {

    private final MoodRepo moodRepo;
    private final UserRepo userRepo;

    private final List<String> LABELS = List.of("", "happy", "calm", "anxious", "sad", "angry", "excited", "tired", "grateful");

    public MoodService(MoodRepo moodRepo, UserRepo userRepo) {
        this.moodRepo = moodRepo;
        this.userRepo = userRepo;
    }

    //get mood history
    public List<MoodResponse> getHistory(Long userId) {
        return moodRepo.findByUserIdOrderByLoggedAtDesc(userId).stream().map(this::toResponse).toList();
    }


    //add mood
    public MoodResponse logMood(Long userId, MoodRequest request) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> (new ResourceNotFoundException("User not found")));

        MoodEntry entry = new MoodEntry();
        entry.setUser(user);
        entry.setMoodScore(request.getMoodScore());
        entry.setMoodLabel(LABELS.get(request.getMoodScore()));
        entry.setNote(request.getNote());
        entry.setLoggedAt(
                request.getLoggedAt() != null ? request.getLoggedAt() : LocalDateTime.now()
        );
        return toResponse(moodRepo.save(entry));
    }

    //get history from range
    public List<MoodResponse> getRange(Long userID, LocalDateTime from, LocalDateTime to) {
        return moodRepo.findByUserIdAndLoggedAtBetweenOrderByLoggedAtAsc(userID, from, to).stream().map(this::toResponse).toList();
    }

    //delete
    public void deleteMood(Long userId, Long entryId) {
        MoodEntry entry = moodRepo.findById(entryId)
                .orElseThrow(() -> new ResourceNotFoundException("Mood entry not found"));

        if (!entry.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only delete your own entries");
        }
        moodRepo.delete(entry);
    }

    private MoodResponse toResponse(MoodEntry e) {
        MoodResponse r = new MoodResponse();
        r.setId(e.getId());
        r.setMoodScore(e.getMoodScore());
        r.setMoodLabel(e.getMoodLabel());
        r.setNote(e.getNote());
        r.setLoggedAt(e.getLoggedAt());
        return r;
    }

}
