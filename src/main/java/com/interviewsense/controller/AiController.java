package com.interviewsense.controller;

import com.interviewsense.dto.AiRequest;
import com.interviewsense.service.AiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/questions")
    public String getQuestions(@RequestBody AiRequest request) {
        return aiService.generateQuestions(request);
    }
}