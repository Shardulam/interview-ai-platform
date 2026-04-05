package com.interviewsense.controller;

import com.interviewsense.dto.AiRequest;
import com.interviewsense.model.InterviewQuestion;
import com.interviewsense.model.User;
import com.interviewsense.repository.InterviewQuestionRepository;
import com.interviewsense.repository.UserRepository;
import com.interviewsense.service.AiService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final AiService aiService;
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final UserRepository userRepository;

    public AiController(AiService aiService,
                        InterviewQuestionRepository interviewQuestionRepository,
                        UserRepository userRepository) {
        this.aiService = aiService;
        this.interviewQuestionRepository = interviewQuestionRepository;
        this.userRepository = userRepository;
    }

    // Generate questions
    @PostMapping("/questions")
    public String generateQuestions(@RequestBody AiRequest request, Authentication auth) {
        return aiService.generateQuestions(request, auth);
    }

    // Get history
    @GetMapping("/history")
    public List<InterviewQuestion> getHistory(Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        return interviewQuestionRepository.findByUserId(user.getId());
    }
}