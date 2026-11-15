
package com.app.ecom.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.ecom.Repository.UserRepository;
import com.app.ecom.dto.LoginRequest;
import com.app.ecom.model.User;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection
    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Login method
    public User login(LoginRequest request) {

        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                    new RuntimeException("Invalid email or password"));

        // Verify raw password against BCrypt hash
        if (!passwordEncoder.matches(
                request.getPassword(), user.getPassword())) {

            throw new RuntimeException("Invalid email or password");
        }

        return user;
    }
}
