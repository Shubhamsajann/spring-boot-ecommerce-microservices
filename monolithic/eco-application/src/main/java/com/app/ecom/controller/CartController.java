
package com.app.ecom.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.ecom.dto.CartItemRequest;
import com.app.ecom.service.CartService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Add product to cart
    @PostMapping
    public ResponseEntity<String> addToCart(
            @RequestBody CartItemRequest request,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Please login first");
        }

        boolean added = cartService.addToCart(userId, request);

        if (!added) {
            return ResponseEntity.badRequest()
                    .body("Unable to add product to cart. Check product and stock.");
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Product added to cart successfully");
    }

    // Get cart items
    @GetMapping
    public ResponseEntity<?> getCartItems(HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Please login first");
        }

        return ResponseEntity.ok(cartService.getCartItems(userId));
    }

    // Remove product from cart
    @DeleteMapping("/{productId}")
    public ResponseEntity<String> removeFromCart(
            @PathVariable Long productId,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Please login first");
        }

        boolean removed = cartService.removeFromCart(userId, productId);

        if (!removed) {
            return ResponseEntity.badRequest()
                    .body("Cart item not found");
        }

        return ResponseEntity.ok("Product removed from cart");
    }
}
