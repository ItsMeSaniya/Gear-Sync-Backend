package com.gearsync.backend.service;

import com.gearsync.backend.exception.DuplicateResourceException;
import com.gearsync.backend.model.User;
import com.gearsync.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gearsync.backend.dto.EmployeeRegisterDTO;

import jakarta.transaction.Transactional;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminServices {

    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIALS = "!@#$%^&*()-_=+[]{}|;:,.<>?";
    private static final String ALL = UPPERCASE + LOWERCASE + DIGITS + SPECIALS;
    private static final SecureRandom random = new SecureRandom();


    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordManagementService passwordManagementService;


    @Transactional
    public Map<String, Object>  addEmployee(EmployeeRegisterDTO employeeRegisterDTO) {
        try {
            if (userRepository.existsByEmail(employeeRegisterDTO.getEmail())) {
                throw new IllegalArgumentException("Email already registered");
            }
            User user = modelMapper.map(employeeRegisterDTO, User.class);
            String generatedPassword = passwordManagementService.generateTemporaryPassword();
            user.setPassword(passwordEncoder.encode(generatedPassword));
            user.setIsFirstLogin(true);
            User savedUser = userRepository.save(user);
            String username = savedUser.getFirstName() + savedUser.getLastName();
            emailService.sendEmployeeWelcomeEmail(savedUser.getEmail(),username,generatedPassword);
            Map<String, Object> response = new HashMap<>();
            response.put("user-email", savedUser.getEmail());
            response.put("message", "Employee added successfully");
            return response;
        } catch (DuplicateResourceException e) {
            throw new DuplicateResourceException("User with email " + employeeRegisterDTO.getEmail() + " already exists.");
        }
    }
}
