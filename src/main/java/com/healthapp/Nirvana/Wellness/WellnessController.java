package com.healthapp.Nirvana.Wellness;


import com.healthapp.Nirvana.User.UserRepo;
import com.healthapp.Nirvana.Wellness.Dto.WellnessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/wellness")
public class WellnessController {

    private final WellnessService wellnessService;
    public final UserRepo userRepo;

    public WellnessController(WellnessService wellnessService, UserRepo userRepo) {
        this.wellnessService = wellnessService;
        this.userRepo = userRepo;
    }


    @GetMapping("/report")
    public ResponseEntity<WellnessResponse> getReport(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(wellnessService.getReport(userId));
    }

    private Long getUserId(UserDetails userDetails) {
        return userRepo.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }

}
