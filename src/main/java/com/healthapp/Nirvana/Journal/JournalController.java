package com.healthapp.Nirvana.Journal;


import com.healthapp.Nirvana.Auth.AuthenticatedUserProvider;
import com.healthapp.Nirvana.Journal.Dto.JournalRequest;
import com.healthapp.Nirvana.Journal.Dto.JournalResponse;
import com.healthapp.Nirvana.User.UserRepo;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/journal")
@CrossOrigin("*")
public class JournalController {

    private final JournalService journalService;
    private final UserRepo userRepo;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public JournalController(JournalService journalService, UserRepo userRepo, AuthenticatedUserProvider authenticatedUserProvider) {
        this.journalService = journalService;
        this.userRepo = userRepo;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }


    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping("/journal")
    public ResponseEntity<JournalResponse> addJournal(@RequestBody @Valid JournalRequest journalRequest,
                                                      @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserId(userDetails);
        return ResponseEntity.status(201).body(journalService.addJournal(userId,journalRequest));

    }

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/journal/history")
    public ResponseEntity<List<JournalResponse>> journalHistory(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(journalService.journalHistory(userId));

    }
    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/doctor/ping")
    public ResponseEntity<?> doctorPing() {
        Long userId = authenticatedUserProvider.getAuthenticatedUserId();
        System.out.println("User ID: " + userId);
        return ResponseEntity.ok("Doctor access confirmed");
    }

    // GET /journal/doctor/history/{patientId}
    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/doctor/{patientId}/history")
    public ResponseEntity<List<JournalResponse>> getJournalHistoryForDoctor(
            @PathVariable Long patientId) {
        Long doctorId = authenticatedUserProvider.getAuthenticatedUserId();
        return ResponseEntity.ok(journalService.getJournalHistoryForDoctor(patientId));
    }

    @PreAuthorize("hasRole('PATIENT')")
    @DeleteMapping("/{journalId}")
    public ResponseEntity<String> deleteJournalEntry(@PathVariable Long journalId,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        journalService.deleteJournalEntry(userId, journalId);
        return ResponseEntity.ok("Journal entry deleted successfully");
    }

    private Long getUserId (UserDetails userDetails){
        return userRepo.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User Not Found"))
                .getId();
    }

}
