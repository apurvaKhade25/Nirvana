package com.healthapp.Nirvana.MoodPrediction;

import com.healthapp.Nirvana.Auth.AuthenticatedUserProvider;
import com.healthapp.Nirvana.User.UserRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/mood/prediction")
public class MoodPredictionController {

    private final MoodInsightService moodInsightService;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final MoodPredictionService moodPredictionService;

    public MoodPredictionController(MoodInsightService moodInsightService, UserRepo userRepo, AuthenticatedUserProvider authenticatedUserProvider, MoodPredictionService moodPredictionService) {
        this.moodInsightService = moodInsightService;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.moodPredictionService = moodPredictionService;
    }

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/predict-mood")
    public ResponseEntity<PatientCycleInsightDto> getInsight(@AuthenticationPrincipal UserDetails userDetails) {
        // Check if the authenticated user is the same as the patientId
        // get current logged in user id from userDetails
        Long userId = authenticatedUserProvider.getAuthenticatedUserId();
        return new ResponseEntity<>(moodInsightService.getMoodInsights(userId), null, 200);

    }

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/predict-mood/next")
    public ResponseEntity<PatientCycleDto> getNextMoodPrediction(@AuthenticationPrincipal UserDetails userDetails) {
        // Check if the authenticated user is the same as the patientId
        // get current logged in user id from userDetails
        Long userId = authenticatedUserProvider.getAuthenticatedUserId();
        return new ResponseEntity<>(moodPredictionService.analyzeMood(userId), null, 200);
    }
}