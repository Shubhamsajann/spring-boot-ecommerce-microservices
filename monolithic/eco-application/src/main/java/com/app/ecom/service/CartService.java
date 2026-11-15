
package com.app.ecom.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.app.ecom.Repository.CartItemRepository;
import com.app.ecom.Repository.ProductRepository;
import com.app.ecom.Repository.UserRepository;
import com.app.ecom.dto.CartItemRequest;
import com.app.ecom.dto.CartItemResponse;
import com.app.ecom.model.CartItem;
import com.app.ecom.model.Product;
import com.app.ecom.model.User;

@Service
public class CartService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;

    // Constructor injection
    public CartService(ProductRepository productRepository,
                       UserRepository userRepository,
                       CartItemRepository cartItemRepository) {

        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartItemRepository = cartItemRepository;
    }

    // Add Product to Cart

public boolean addToCart(Long userId, CartItemRequest request) {

    // Find product
    Optional<Product> productOptional =
            productRepository.findById(request.getProductId());

    if (productOptional.isEmpty()) {
        return false;
    }

    Product product = productOptional.get();

    // Check whether product is active
    if (!Boolean.TRUE.equals(product.getActive())) {
        return false;
    }

    // Check stock availability and quantity
    if (request.getQuantity() == null || request.getQuantity() <= 0
            || product.getStockQuantity() == null
            || product.getStockQuantity() < request.getQuantity()) {
        return false;
    }

    // Find user
    Optional<User> userOptional = userRepository.findById(userId);

    if (userOptional.isEmpty()) {
        return false;
    }

    User user = userOptional.get();

    // Check whether product already exists in cart
    Optional<CartItem> existingCartItem =
            cartItemRepository.findByUserAndProduct(user, product);

    CartItem cartItem;

    if (existingCartItem.isPresent()) {

        cartItem = existingCartItem.get();

        int newQuantity =
                cartItem.getQuantity() + request.getQuantity();

        // Check combined quantity against stock
        if (newQuantity > product.getStockQuantity()) {
            return false;
        }

        cartItem.setQuantity(newQuantity);

    } else {

        cartItem = new CartItem();
        cartItem.setUser(user);
        cartItem.setProduct(product);
        cartItem.setQuantity(request.getQuantity());
        cartItem.setPrice(product.getPrice());
    }

    // Save cart item
    cartItemRepository.save(cartItem);

    return true;
}

public BigDecimal calculateTotalCartPrice(Long userId) {

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

    List<CartItem> cartItems =
            cartItemRepository.findByUser(user);

    return cartItems.stream()
            .map(item -> item.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
}


public List<CartItemResponse> getCartItems(Long userId) {

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

    List<CartItem> cartItems = cartItemRepository.findByUser(user);

    return cartItems.stream()
            .map(item -> {

                BigDecimal totalPrice = item.getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()));

                return new CartItemResponse(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPrice(),
                        totalPrice
                );
            })
            .toList();
}

public boolean removeFromCart(Long userId, Long productId) {

    Optional<User> userOptional = userRepository.findById(userId);

    if (userOptional.isEmpty()) {
        return false;
    }

    Optional<Product> productOptional = productRepository.findById(productId);

    if (productOptional.isEmpty()) {
        return false;
    }

    Optional<CartItem> cartItemOptional =
            cartItemRepository.findByUserAndProduct(
                    userOptional.get(), productOptional.get());

    if (cartItemOptional.isEmpty()) {
        return false;
    }

    cartItemRepository.delete(cartItemOptional.get());

    return true;
}

}
