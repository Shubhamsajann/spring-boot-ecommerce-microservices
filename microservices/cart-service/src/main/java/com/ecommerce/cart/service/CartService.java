
package com.ecommerce.cart.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.cart.dto.CartItemRequest;
import com.ecommerce.cart.dto.CartItemResponse;
import com.ecommerce.cart.dto.CartResponse;
import com.ecommerce.cart.model.Cart;
import com.ecommerce.cart.model.CartItem;
import com.ecommerce.cart.repository.CartRepository;
import com.ecommerce.cart.repository.CartItemRepository;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    // Add product to cart
    public CartResponse addToCart(
            Long userId, CartItemRequest request) {

        if (userId == null || userId <= 0
                || request.getProductId() == null
                || request.getQuantity() == null
                || request.getQuantity() <= 0) {

            throw new IllegalArgumentException(
                    "Valid user ID, product ID and quantity are required");
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return cartRepository.save(newCart);
                });

        CartItem item = cartItemRepository
                .findByCartIdAndProductId(
                        cart.getId(), request.getProductId())
                .orElse(null);

        if (item != null) {

            item.setQuantity(
                    item.getQuantity() + request.getQuantity());

            cartItemRepository.save(item);

        } else {

            item = new CartItem();
            item.setProductId(request.getProductId());
            item.setQuantity(request.getQuantity());

            cart.addItem(item);
            cartRepository.save(cart);
        }

        return getCart(userId);
    }

    // Get user's cart
    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("Cart not found"));

        List<CartItemResponse> items =
                cart.getItems().stream()
                .map(item -> new CartItemResponse(
                        item.getId(),
                        item.getProductId(),
                        item.getQuantity()))
                .toList();

        return new CartResponse(
                cart.getId(),
                cart.getUserId(),
                items);
    }

    // Update product quantity
    public CartResponse updateQuantity(
            Long userId, Long productId, Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero");
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("Cart not found"));

        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() ->
                        new RuntimeException("Cart item not found"));

        item.setQuantity(quantity);
        cartItemRepository.save(item);

        return getCart(userId);
    }

    // Remove product from cart
    public void removeItem(Long userId, Long productId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("Cart not found"));

        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() ->
                        new RuntimeException("Cart item not found"));

        cart.removeItem(item);
        cartRepository.save(cart);
    }

    // Clear entire cart
    public void clearCart(Long userId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("Cart not found"));

        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
