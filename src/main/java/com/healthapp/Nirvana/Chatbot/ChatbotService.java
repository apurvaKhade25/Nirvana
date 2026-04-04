package com.healthapp.Nirvana.Chatbot;

import com.healthapp.Nirvana.Ai.AiService;
import com.healthapp.Nirvana.Chatbot.Dto.ChatbotRequest;
import com.healthapp.Nirvana.Chatbot.Dto.ChatbotResponse;
import com.healthapp.Nirvana.Journal.JournalEntry;
import com.healthapp.Nirvana.Journal.JournalRepo;
import com.healthapp.Nirvana.Mood.MoodEntry;
import com.healthapp.Nirvana.Mood.MoodRepo;
import com.healthapp.Nirvana.User.User;
import com.healthapp.Nirvana.User.UserRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class ChatbotService {
    private final MoodRepo moodRepo;
    private final ChatRepo chatRepo;
    private final AiService aiService;
    private final JournalRepo journalRepo;
    private final UserRepo userRepo;

    public ChatbotService(MoodRepo moodRepo, ChatRepo chatRepo, AiService aiService, JournalRepo journalRepo, UserRepo userRepo) {
        this.moodRepo = moodRepo;
        this.chatRepo = chatRepo;
        this.aiService = aiService;
        this.journalRepo = journalRepo;
        this.userRepo = userRepo;
    }

    public ChatbotResponse chatbotResponse(Long userId, String message) {
        //last 10 messages
        List<ChatEntry> history = chatRepo.findTop10ByUserIdOrderBySentAtDesc(userId);

        //get mood summary
        List<MoodEntry> moods = moodRepo.findByUserIdOrderByLoggedAtAsc(userId);
        double avg = moods.stream()
                .mapToInt(MoodEntry::getMoodScore)
                .average().orElse(0);
        String topMood = moods.isEmpty() ? "unknown" : moods.get(0).getMoodLabel();

        //get journal summary
        List<JournalEntry> journalEntries = journalRepo.findByUserIdOrderByCreatedAtDesc(userId);
        String recentJournal = journalEntries.isEmpty() ? "none" : journalEntries.get(0).getContent();

        //build ai context
        String context = String.format("You are Nirvana, a warm and caring AI companion — like a close friend, not a therapist.\n" +
                "User's mood average: %.1f/10. Most recent mood: %s.\n" +
                "Recent journal: \"%s\".\n\n" +
                "STRICT RULES:\n" +
                "- Keep replies SHORT — 2 to 4 sentences maximum\n" +
                "- Be warm, casual, and human — like texting a supportive friend\n" +
                "- NEVER use clinical words like 'metacognitive', 'distress', 'emotional arc'\n" +
                "- NEVER write long lists or bullet points\n" +
                "- Celebrate wins with the user, don't analyse them\n" +
                "- If someone says something good, just react naturally — 'That's amazing!!'\n" +
                "- Match the user's energy — if they're casual, be casual\n" +
                "- Never give medical advice\n" +
                "IMPORTANT: Always talk DIRECTLY to the user. Say 'you' not 'the user'.\n", avg, topMood,
                recentJournal);

        StringBuilder fullPrompt = new StringBuilder(context);


        //chat hisory
        List<ChatEntry> chronological = history.stream().sorted(Comparator.comparing(ChatEntry::getSentAt)).toList();

        //add chat history
        for (ChatEntry entry : chronological) {
            if (entry.getRole().equals("user")) {
                fullPrompt.append("User: ").append(entry.getMessage()).append("\n");
            } else {
                fullPrompt.append("Nirvana: ").append(entry.getMessage()).append("\n");
            }
        }
        String aireply = aiService.generateInsight(fullPrompt.toString());

        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("USER ID = " + userId);
        //save user message
        ChatEntry userMsg = new ChatEntry();
//        userMsg.setUser(userRepo.findById(userId).orElseThrow());
        userMsg.setUser(user);
        userMsg.setRole("user");
        userMsg.setMessage(message);
        userMsg.setSentAt(LocalDateTime.now());
        chatRepo.save(userMsg);

        //save ai message
        ChatEntry aiMsg = new ChatEntry();
//        aiMsg.setUser(userRepo.findById(userId).orElseThrow());
        aiMsg.setUser(user);
        aiMsg.setRole("assistant");
        aiMsg.setMessage(aireply);
        aiMsg.setSentAt(LocalDateTime.now());
        chatRepo.save(aiMsg);

        //return response
        ChatbotResponse chatbotResponse = new ChatbotResponse();
        chatbotResponse.setMessage(aireply);
        chatbotResponse.setSentAt(LocalDateTime.now());
        return chatbotResponse;

    }

    public List<ChatbotResponse> getHistory(Long userId) {
        List<ChatEntry> chats =
                chatRepo.findTop10ByUserIdOrderBySentAtDesc(userId);
        return chats.stream().map(chat -> {
            ChatbotResponse res = new ChatbotResponse();
            res.setMessage(chat.getMessage());
            res.setRole(chat.getRole());
            res.setSentAt(chat.getSentAt());
            return res;
        }).toList();

    }
}
