package com.healthapp.Nirvana.Mood;

import com.healthapp.Nirvana.Auth.UserPrinciple;
import com.healthapp.Nirvana.Mood.Dto.MoodRequest;
import com.healthapp.Nirvana.Mood.Dto.MoodResponse;
import com.healthapp.Nirvana.User.UserRepo;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.healthapp.Nirvana.Auth.AuthenticatedUserProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/mood")
@CrossOrigin("*")
public class MoodController {
    private final MoodService moodService;
    private final UserRepo userRepo;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public MoodController(MoodService moodService, UserRepo userRepo, AuthenticatedUserProvider authenticatedUserProvider) {
        this.moodService = moodService;
        this.userRepo = userRepo;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    // GET /mood/history
    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/history")
    public ResponseEntity<List<MoodResponse>> getHistory(
            @AuthenticationPrincipal UserDetails userDetails) { // gets logged in user
        Long userId = authenticatedUserProvider.getAuthenticatedUserId();
        System.out.println("User ID: " + userId);

        return ResponseEntity.ok(moodService.getHistory(userId));
    }

    // POST /mood
    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping
    public ResponseEntity<MoodResponse> logMood(
            @RequestBody @Valid MoodRequest request,        // receives and validates json
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserId(userDetails);
        return ResponseEntity.status(201).body(moodService.logMood(userId, request)); // ← pass request
    }

    // GET /mood/range?from=...&to=...
    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/range")
    public ResponseEntity<List<MoodResponse>> getRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate to,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(moodService.getRange(userId, from.atStartOfDay(), to.atTime(23, 59, 59)));
    }

    // DELETE /mood/{id}
    @PreAuthorize("hasRole('PATIENT')")
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
