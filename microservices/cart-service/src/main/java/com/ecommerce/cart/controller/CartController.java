
package com.ecommerce.cart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.cart.dto.CartItemRequest;
import com.ecommerce.cart.dto.CartResponse;
import com.ecommerce.cart.service.CartService;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Add product to cart
    @PostMapping("/{userId}/items")
    public ResponseEntity<CartResponse> addToCart(
            @PathVariable Long userId,
            @RequestBody CartItemRequest request) {

        return ResponseEntity.ok(
                cartService.addToCart(userId, request));
    }

    // Get user's cart
    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getCart(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                cartService.getCart(userId));
    }

    // Update product quantity
    @PutMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartResponse> updateQuantity(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @RequestParam Integer quantity) {

        return ResponseEntity.ok(
                cartService.updateQuantity(
                        userId, productId, quantity));
    }

    // Remove product from cart
    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<Void> removeItem(
            @PathVariable Long userId,
            @PathVariable Long productId) {

        cartService.removeItem(userId, productId);

        return ResponseEntity.noContent().build();
    }

    // Clear entire cart
    @DeleteMapping("/{userId}/items")
    public ResponseEntity<Void> clearCart(
            @PathVariable Long userId) {

        cartService.clearCart(userId);

        return ResponseEntity.noContent().build();
    }
}
