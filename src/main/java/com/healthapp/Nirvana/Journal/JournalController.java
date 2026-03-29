package com.healthapp.Nirvana.Journal;


import com.healthapp.Nirvana.Journal.Dto.JournalRequest;
import com.healthapp.Nirvana.Journal.Dto.JournalResponse;
import com.healthapp.Nirvana.User.UserRepo;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/")
@CrossOrigin("*")
public class JournalController {

    private final JournalService journalService;
    private final UserRepo userRepo;

    public JournalController(JournalService journalService, UserRepo userRepo) {
        this.journalService = journalService;
        this.userRepo = userRepo;
    }


    @PostMapping("/journal")
    public ResponseEntity<JournalResponse> addJournal(@RequestBody @Valid JournalRequest journalRequest,
                                                      @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserId(userDetails);
        return ResponseEntity.status(201).body(journalService.addJournal(userId,journalRequest));

    }

    @GetMapping("/journal/history")
    public ResponseEntity<List<JournalResponse>> journalHistory(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(journalService.journalHistory(userId));

    }

    private Long getUserId (UserDetails userDetails){
        return userRepo.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User Not Found"))
                .getId();
    }

}
