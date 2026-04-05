package com.interviewsense.service;

import com.interviewsense.dto.AiRequest;
import com.interviewsense.model.InterviewQuestion;
import com.interviewsense.model.User;
import com.interviewsense.repository.InterviewQuestionRepository;
import com.interviewsense.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.ByteArrayOutputStream;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    @Value("${groq.api.key}")
    private String apiKey;

    private final InterviewQuestionRepository interviewQuestionRepository;
    private final UserRepository userRepository;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.groq.com/openai/v1")
            .build();

    public AiService(InterviewQuestionRepository interviewQuestionRepository,
                     UserRepository userRepository) {
        this.interviewQuestionRepository = interviewQuestionRepository;
        this.userRepository = userRepository;
    }

    public String generateQuestions(AiRequest request, Authentication auth) {

        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();

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
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        List choices = (List) response.get("choices");
        Map choice = (Map) choices.get(0);
        Map message = (Map) choice.get("message");

        String aiResponse = message.get("content").toString();

        // SAVE TO DATABASE
        InterviewQuestion iq = new InterviewQuestion();
        iq.setUserId(user.getId());
        iq.setRole(request.getRole());
        iq.setExperience(request.getExperience());
        iq.setSkills(request.getSkills());
        iq.setQuestions(aiResponse);

        interviewQuestionRepository.save(iq);

        return aiResponse;
    }
    public String generateQuestionsFromResume(MultipartFile file, Authentication auth) throws Exception {

    // Get logged-in user
    String email = auth.getName();
    User user = userRepository.findByEmail(email).orElseThrow();

    // Extract text from PDF
    InputStream inputStream = file.getInputStream();
    PDDocument document = PDDocument.load(inputStream);
    PDFTextStripper pdfStripper = new PDFTextStripper();
    String resumeText = pdfStripper.getText(document);
    document.close();

    // Create AI prompt
    String prompt = "Generate 10 interview questions based on this resume:\n" + resumeText;

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("model", "llama-3.1-8b-instant");
    requestBody.put("messages", List.of(
            Map.of("role", "user", "content", prompt)
    ));

    Map response = webClient.post()
            .uri("/chat/completions")
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(Map.class)
            .block();

    List choices = (List) response.get("choices");
    Map choice = (Map) choices.get(0);
    Map message = (Map) choice.get("message");

    String aiResponse = message.get("content").toString();

    // Save to DB
    InterviewQuestion iq = new InterviewQuestion();
    iq.setUserId(user.getId());
    iq.setRole("From Resume");
    iq.setExperience("From Resume");
    iq.setSkills("Extracted from Resume");
    iq.setQuestions(aiResponse);

    interviewQuestionRepository.save(iq);

    return aiResponse;
}
public byte[] downloadInterviewPdf(Long id) throws Exception {

    InterviewQuestion iq = interviewQuestionRepository.findById(id).orElseThrow();

    PDDocument document = new PDDocument();
    PDPage page = new PDPage();
    document.addPage(page);

    PDPageContentStream contentStream = new PDPageContentStream(document, page);

    contentStream.beginText();
    contentStream.setFont(PDType1Font.HELVETICA, 12);
    contentStream.setLeading(14.5f);
    contentStream.newLineAtOffset(50, 700);

    contentStream.showText("Interview Questions");
    contentStream.newLine();
    contentStream.showText("Role: " + iq.getRole());
    contentStream.newLine();
    contentStream.showText("Experience: " + iq.getExperience());
    contentStream.newLine();
    contentStream.showText("Skills: " + iq.getSkills());
    contentStream.newLine();
    contentStream.newLine();

    String[] lines = iq.getQuestions().split("\n");
    for (String line : lines) {
        contentStream.showText(line);
        contentStream.newLine();
    }

    contentStream.endText();
    contentStream.close();

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    document.save(out);
    document.close();

    return out.toByteArray();
}
}