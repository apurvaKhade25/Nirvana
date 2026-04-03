package com.healthapp.Nirvana.Chatbot;

import com.healthapp.Nirvana.Chatbot.Dto.ChatbotRequest;
import com.healthapp.Nirvana.Chatbot.Dto.ChatbotResponse;
import com.healthapp.Nirvana.User.UserRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@CrossOrigin("*")
@RequestMapping("/chatbot")
public class ChatbotController {

    public final ChatbotService chatbotService;
    public final UserRepo userRepo;

    public ChatbotController(ChatbotService chatbotService, UserRepo userRepo) {
        this.chatbotService = chatbotService;
        this.userRepo = userRepo;
    }

    @PostMapping
    public ResponseEntity<ChatbotResponse> chat(@RequestBody ChatbotRequest chatbotRequest,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(chatbotService.chatbotResponse(userId, chatbotRequest.getMessage()));
    }


    @GetMapping("/history")
    public ResponseEntity<List<ChatbotResponse>> getHistory(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(chatbotService.getHistory(userId));
    }

    private Long getUserId(UserDetails userDetails) {
        return userRepo.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }
}
