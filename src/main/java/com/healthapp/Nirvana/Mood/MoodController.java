package com.healthapp.Nirvana.Mood;

import com.healthapp.Nirvana.Mood.Dto.MoodRequest;
import com.healthapp.Nirvana.Mood.Dto.MoodResponse;
import com.healthapp.Nirvana.User.UserRepo;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/mood")
@CrossOrigin("*")
public class MoodController {
    private final MoodService moodService;
    private final UserRepo userRepo;

    public MoodController(MoodService moodService, UserRepo userRepo) {
        this.moodService = moodService;
        this.userRepo = userRepo;
    }

    // GET /mood/history
    @GetMapping("/history")
    public ResponseEntity<List<MoodResponse>> getHistory(
            @AuthenticationPrincipal UserDetails userDetails) { // gets logged in user

        Long userId = getUserId(userDetails);  //gets userid from token
        return ResponseEntity.ok(moodService.getHistory(userId));
    }

    // POST /mood
    @PostMapping
    public ResponseEntity<MoodResponse> logMood(
            @RequestBody @Valid MoodRequest request,        // receives and validates json
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserId(userDetails);
        return ResponseEntity.status(201).body(moodService.logMood(userId, request)); // ← pass request
    }

    // GET /mood/range?from=...&to=...
    @GetMapping("/range")
    public ResponseEntity<List<MoodResponse>> getRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate to,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(moodService.getRange(userId, from.atStartOfDay(), to.atTime(23, 59, 59)));
    }

    // DELETE /mood/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserId(userDetails);
        moodService.deleteMood(userId, id);
        return ResponseEntity.ok("Mood entry deleted");
    }

    // helper
    private Long getUserId(UserDetails userDetails) {
        return userRepo.findByEmail(userDetails.getUsername())

                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }
}
