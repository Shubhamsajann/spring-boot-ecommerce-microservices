
package com.app.ecom.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.ecom.dto.OrderResponse;
import com.app.ecom.service.OrderService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    // Constructor injection
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Create order
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            HttpSession session) {

        // Get logged-in user ID from session
        Long userId = (Long) session.getAttribute("userId");

        // Check whether user is logged in
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        // Create order
        OrderResponse response = orderService.createOrder(userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

@GetMapping
public ResponseEntity<?> getOrderHistory(HttpSession session) {

    Long userId = (Long) session.getAttribute("userId");

    if (userId == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Please login first");
    }

    return ResponseEntity.ok(
            orderService.getOrderHistory(userId)
    );
}
@GetMapping("/{orderId}")
public ResponseEntity<?> getOrderById(
        @PathVariable Long orderId,
        HttpSession session) {

    Long userId = (Long) session.getAttribute("userId");

    if (userId == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Please login first");
    }

    return ResponseEntity.ok(
            orderService.getOrderById(orderId, userId)
    );
}
}
