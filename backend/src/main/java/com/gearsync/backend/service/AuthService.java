package com.gearsync.backend.service;

import com.gearsync.backend.model.User;
import com.gearsync.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean isEmailRegistered(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User register(User user) {
        // encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public boolean authenticate(String email, String rawPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return false;
        User user = userOpt.get();
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

}