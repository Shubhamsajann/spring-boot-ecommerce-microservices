
package com.app.ecom.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.ecom.Repository.CartItemRepository;
import com.app.ecom.Repository.OrderRepository;
import com.app.ecom.Repository.ProductRepository;
import com.app.ecom.Repository.UserRepository;
import com.app.ecom.dto.OrderItemResponse;
import com.app.ecom.dto.OrderResponse;
import com.app.ecom.model.CartItem;
import com.app.ecom.model.Order;
import com.app.ecom.model.OrderItem;
import com.app.ecom.model.OrderStatus;
import com.app.ecom.model.Product;
import com.app.ecom.model.User;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    // Constructor injection
    public OrderService(
            OrderRepository orderRepository,
            CartItemRepository cartItemRepository,
            UserRepository userRepository,
            ProductRepository productRepository) {

        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // Create order from cart
    @Transactional
    public OrderResponse createOrder(Long userId) {

        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                    new RuntimeException("User not found"));

        // Get cart items
        List<CartItem> cartItems =
                cartItemRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Your cart is empty");
        }

        // Create new order
        Order order = new Order();

        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;

        List<OrderItemResponse> itemResponses =
                new ArrayList<>();

        // Process each cart item
        for (CartItem cartItem : cartItems) {

            Product product = productRepository.findById(
                    cartItem.getProduct().getId())
                    .orElseThrow(() ->
                        new RuntimeException("Product not found"));

            // Validate stock and product status
            if (Boolean.FALSE.equals(product.getActive())
                    || cartItem.getQuantity() == null
                    || cartItem.getQuantity() <= 0
                    || product.getStockQuantity() == null
                    || product.getStockQuantity() < cartItem.getQuantity()) {

                throw new RuntimeException(
                        "Insufficient stock or inactive product: "
                        + product.getName());
            }

            // Calculate item total using cart price
            BigDecimal itemTotal = cartItem.getPrice()
                    .multiply(BigDecimal.valueOf(
                            cartItem.getQuantity()));

            totalAmount = totalAmount.add(itemTotal);

            // Create order item
            OrderItem orderItem = new OrderItem();

            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());

            order.addItem(orderItem);

            // Reduce product stock
            product.setStockQuantity(
                    product.getStockQuantity()
                    - cartItem.getQuantity());

            productRepository.save(product);

            // Prepare item response
            itemResponses.add(new OrderItemResponse(
                    product.getId(),
                    product.getName(),
                    cartItem.getQuantity(),
                    cartItem.getPrice(),
                    itemTotal));
        }

        // Set total amount
        order.setTotalAmount(totalAmount);

        // Save order and order items
        Order savedOrder = orderRepository.save(order);

        // Clear cart after successful order creation
        cartItemRepository.deleteAll(cartItems);

        // Return response
        return new OrderResponse(
                savedOrder.getId(),
                savedOrder.getTotalAmount(),
                savedOrder.getStatus(),
                savedOrder.getCreatedAt(),
                itemResponses);
    }

public List<OrderResponse> getOrderHistory(Long userId) {

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

    List<Order> orders =
            orderRepository.findByUserOrderByCreatedAtDesc(user);

    return orders.stream().map(order -> {

        List<OrderItemResponse> items = order.getItems()
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getPrice().multiply(
                                BigDecimal.valueOf(item.getQuantity()))
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                items
        );

    }).toList();
}

public OrderResponse getOrderById(Long orderId, Long userId) {

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

    Order order = orderRepository.findByIdAndUser(orderId, user)
            .orElseThrow(() -> new RuntimeException("Order not found"));

    List<OrderItemResponse> items = order.getItems()
            .stream()
            .map(item -> new OrderItemResponse(
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getPrice(),
                    item.getPrice().multiply(
                            BigDecimal.valueOf(item.getQuantity()))
            ))
            .toList();

    return new OrderResponse(
            order.getId(),
            order.getTotalAmount(),
            order.getStatus(),
            order.getCreatedAt(),
            items
    );
}

}
