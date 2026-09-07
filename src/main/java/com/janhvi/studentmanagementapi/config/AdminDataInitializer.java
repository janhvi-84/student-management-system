package com.janhvi.studentmanagementapi.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.janhvi.studentmanagementapi.entity.User;
import com.janhvi.studentmanagementapi.repository.UserRepository;

@Configuration
public class AdminDataInitializer {

    @Bean
    CommandLineRunner createAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // Check karo admin pehle se exist karta hai ya nahi
            if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {

                User admin = new User();

                admin.setEmail("admin@gmail.com");

                // Password BCrypt se encrypt hoga
                admin.setPassword(
                        passwordEncoder.encode("admin123")
                );

                admin.setRole("ADMIN");

                userRepository.save(admin);

                System.out.println("=================================");
                System.out.println("ADMIN CREATED SUCCESSFULLY");
                System.out.println("Email: admin@gmail.com");
                System.out.println("Password: admin123");
                System.out.println("Role: ADMIN");
                System.out.println("=================================");

            } else {

                System.out.println("ADMIN ALREADY EXISTS");

            }
        };
    }
}