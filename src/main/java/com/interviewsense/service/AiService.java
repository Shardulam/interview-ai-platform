package com.interviewsense.service;

import com.interviewsense.dto.AiRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    @Value("${groq.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.groq.com/openai/v1")
            .build();

    public String generateQuestions(AiRequest request) {

    String prompt = "Generate 10 interview questions for a "
            + request.getRole()
            + " with "
            + request.getExperience()
            + " experience. Skills: "
            + request.getSkills();

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("model", "llama-3.1-8b-instant");
    requestBody.put("messages", List.of(
            Map.of("role", "user", "content", prompt)
    ));

    Map response = webClient.post()
            .uri("/chat/completions")
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(Map.class)
            .block();

    List choices = (List) response.get("choices");
    Map choice = (Map) choices.get(0);
    Map message = (Map) choice.get("message");

    return message.get("content").toString();
}
}