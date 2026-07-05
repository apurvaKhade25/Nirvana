package com.healthapp.Nirvana.Journal;

import com.healthapp.Nirvana.Auth.AuthenticatedUserProvider;
import com.healthapp.Nirvana.Consent.ConsentGuard;
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
    public final ConsentGuard consentGuard;
    public final AuthenticatedUserProvider authenticatedUserProvider;

    public JournalService(JournalRepo journalRepo, UserRepo userRepo, ConsentGuard consentGuard, AuthenticatedUserProvider authenticatedUserProvider) {
        this.journalRepo = journalRepo;
        this.userRepo = userRepo;
        this.consentGuard = consentGuard;
        this.authenticatedUserProvider = authenticatedUserProvider;
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
        return journalRepo.findByUserIdOrderByCreatedAtAsc(userId).stream().map(this::toresponse).toList();
    }

    // get joural history for doctor with consent
    public List<JournalResponse> getJournalHistoryForDoctor(Long patientId) {
        Long doctorId = authenticatedUserProvider.getAuthenticatedUserId();
        User patient = userRepo.findById(patientId).orElseThrow(() -> new RuntimeException("Patient not found"));
        consentGuard.verify(doctorId, patientId); // Check if the doctor has consent
        return journalRepo.findByUserIdOrderByCreatedAtAsc(patientId).stream().map(this::toresponse).toList();
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

