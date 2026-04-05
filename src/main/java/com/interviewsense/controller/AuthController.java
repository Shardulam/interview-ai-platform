package com.interviewsense.controller;

import com.interviewsense.dto.LoginRequest;
import com.interviewsense.jwt.JwtUtil;
import com.interviewsense.model.User;
import com.interviewsense.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    public User signup(@RequestBody SignupRequest request) {
        System.out.println("SIGNUP API HIT");
        return userService.registerUser(request.name(), request.email(), request.password());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        System.out.println("LOGIN API HIT");
        System.out.println("Email: " + request.getEmail());
        System.out.println("Password: " + request.getPassword());

        User user = userService.authenticate(request.getEmail(), request.getPassword());

        if (user != null) {
            String token = jwtUtil.generateToken(user.getEmail());
            return ResponseEntity.ok(Map.of("token", token));
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    public static record SignupRequest(String name, String email, String password) {}
}