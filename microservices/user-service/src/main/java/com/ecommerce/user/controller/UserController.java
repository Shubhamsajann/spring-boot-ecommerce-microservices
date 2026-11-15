
package com.ecommerce.user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.user.dto.LoginRequest;
import com.ecommerce.user.dto.UserRequest;
import com.ecommerce.user.dto.UserResponse;
import com.ecommerce.user.service.UserService;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Fetch all users
    @GetMapping("/api/users")
    public ResponseEntity<List<UserResponse>> getAllUser() {
        return ResponseEntity.ok(userService.fetchAllUser());
    }

    // Fetch user by ID
    @GetMapping("/api/users/{id}")
    public ResponseEntity<UserResponse> getUser(
            @PathVariable Long id) {

        return userService.fetchUser(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Register user
    @PostMapping("/api/users")
    public ResponseEntity<UserResponse> createUser(
            @RequestBody UserRequest request) {

        UserResponse savedUser = userService.addUser(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedUser);
    }

    // Update user
    @PutMapping("/api/users/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody UserRequest request) {

        return userService.updateUser(id, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // User Login
    @PostMapping("/api/users/login")
    public ResponseEntity<String> loginUser(
            @RequestBody LoginRequest request) {

        boolean authenticated = userService.loginUser(request);

        if (authenticated) {
            return ResponseEntity.ok("Login successful");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid email or password");
    }
}
