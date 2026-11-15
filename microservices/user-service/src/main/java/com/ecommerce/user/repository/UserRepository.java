package com.ecommerce.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    // Find user using email
    Optional<User> findByEmail(String email);

    // Check whether email already exists
    boolean existsByEmail(String email);

}