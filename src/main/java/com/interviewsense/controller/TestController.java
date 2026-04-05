package com.interviewsense.controller;

import com.interviewsense.repository.UserRepository;
import com.interviewsense.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class TestController {

    private final UserRepository userRepository;

    public TestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/check")
    public String checkUser() {
        Optional<User> user = userRepository.findByEmail("sathvika@gmail.com");
        if (user.isPresent()) {
            return "User found: " + user.get().getEmail() + " " + user.get().getPassword();
        } else {
            return "User NOT found";
        }
    }
}