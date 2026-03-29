package com.healthapp.Nirvana.Journal;

import com.healthapp.Nirvana.Journal.Dto.JournalRequest;
import com.healthapp.Nirvana.Journal.Dto.JournalResponse;
import com.healthapp.Nirvana.Mood.Dto.MoodResponse;
import com.healthapp.Nirvana.Mood.MoodEntry;
import com.healthapp.Nirvana.User.User;
import com.healthapp.Nirvana.User.UserRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JournalService {

    public final JournalRepo journalRepo;
    public final UserRepo userRepo;

    public JournalService(JournalRepo journalRepo, UserRepo userRepo) {
        this.journalRepo = journalRepo;
        this.userRepo = userRepo;
    }


    public JournalResponse addJournal(Long userId, JournalRequest journalRequest){
        User user= userRepo.findById(userId).orElseThrow(()-> new RuntimeException("User not found"));

        JournalEntry journalEntry = new JournalEntry();
        journalEntry.setUser(user);
        journalEntry.setTitle(journalRequest.getTitle());
        journalEntry.setContent(journalRequest.getContent());
        journalEntry.setCreatedAt(LocalDateTime.now());

        return toresponse(journalRepo.save(journalEntry));
    }

    public List<JournalResponse> journalHistory(Long userId){
        return journalRepo.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toresponse).toList();
    }


    private JournalResponse toresponse(JournalEntry e) {
        JournalResponse r = new JournalResponse();
        r.setId(e.getId());
        r.setTitle(e.getTitle());
        r.setContent(e.getContent());
        r.setCreatedAt(e.getCreatedAt());
        return r;
    }
}

