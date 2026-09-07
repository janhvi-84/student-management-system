package com.janhvi.studentmanagementapi.service;

import com.janhvi.studentmanagementapi.entity.User;
import com.janhvi.studentmanagementapi.repository.UserRepository;
import com.janhvi.studentmanagementapi.security.JwtService;

import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuditService(
            JwtService jwtService,
            UserRepository userRepository) {

        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    // ========================================
    // GET CURRENT LOGGED-IN USER ID
    // ========================================

    public Long getCurrentUserId(String token) {

        if (token == null || token.isBlank()) {
            throw new RuntimeException(
                    "Authorization token missing"
            );
        }

        // Remove "Bearer "
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // JWT subject = email
        String email =
                jwtService.extractUsername(token);

        User user =
                userRepository
                        .findByEmailAndDeletedFalse(email)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Logged-in user not found"
                                )
                        );

        return user.getId();
    }
}