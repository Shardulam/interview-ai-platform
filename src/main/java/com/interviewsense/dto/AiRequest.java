package com.interviewsense.dto;

public class AiRequest {
    private String role;
    private String experience;
    private String skills;

    public AiRequest() {
    }

    public AiRequest(String role, String experience, String skills) {
        this.role = role;
        this.experience = experience;
        this.skills = skills;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }
}
