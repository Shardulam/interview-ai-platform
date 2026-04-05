package com.interviewsense.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interview_questions")
public class InterviewQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String role;
    private String experience;

    @Column(columnDefinition = "TEXT")
    private String skills;

    @Column(columnDefinition = "TEXT")
    private String questions;

    private LocalDateTime createdAt = LocalDateTime.now();

    public InterviewQuestion() {}

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getRole() { return role; }
    public String getExperience() { return experience; }
    public String getSkills() { return skills; }
    public String getQuestions() { return questions; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setUserId(Long userId) { this.userId = userId; }
    public void setRole(String role) { this.role = role; }
    public void setExperience(String experience) { this.experience = experience; }
    public void setSkills(String skills) { this.skills = skills; }
    public void setQuestions(String questions) { this.questions = questions; }
}