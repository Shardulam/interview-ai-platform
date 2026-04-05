package com.interviewsense.service;

import com.interviewsense.model.User;
import com.interviewsense.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(String name, String email, String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole("USER");
        return userRepository.save(user);
    }

    public User authenticate(String email, String password) {
    System.out.println("LOGIN TRY -> " + email + " | " + password);

    Optional<User> userOpt = userRepository.findByEmail(email);

    if (userOpt.isPresent()) {
        User user = userOpt.get();
        System.out.println("DB USER -> " + user.getEmail() + " | " + user.getPassword());

        if (password.equals(user.getPassword())) {
            System.out.println("LOGIN SUCCESS");
            return user;
        } else {
            System.out.println("PASSWORD WRONG");
        }
    } else {
        System.out.println("USER NOT FOUND");
    }

    return null;
}
}
