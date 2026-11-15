package com.ecommerce.order.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.order.dto.*;
import com.ecommerce.order.model.*;
import com.ecommerce.order.repository.OrderRepository;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Create order
    public OrderResponse createOrder(OrderRequest request) {

        if (request.getUserId() == null
                || request.getUserId() <= 0
                || request.getItems() == null
                || request.getItems().isEmpty()) {

            throw new IllegalArgumentException(
                    "Valid user ID and order items are required");
        }

        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {

            if (itemRequest.getProductId() == null
                    || itemRequest.getProductId() <= 0
                    || itemRequest.getQuantity() == null
                    || itemRequest.getQuantity() <= 0
                    || itemRequest.getPrice() == null
                    || itemRequest.getPrice().compareTo(BigDecimal.ZERO) <= 0
                    || itemRequest.getProductName() == null
                    || itemRequest.getProductName().isBlank()) {

                throw new IllegalArgumentException(
                        "Valid product details are required");
            }

            OrderItem item = new OrderItem();

            item.setProductId(itemRequest.getProductId());
            item.setProductName(itemRequest.getProductName());
            item.setQuantity(itemRequest.getQuantity());
            item.setPrice(itemRequest.getPrice());

            BigDecimal itemTotal = itemRequest.getPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            item.setTotalPrice(itemTotal);

            order.addItem(item);

            totalAmount = totalAmount.add(itemTotal);
        }

        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        return convertToResponse(savedOrder);
    }

    // Get order by ID
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        return convertToResponse(order);
    }

    // Get orders by user ID
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(Long userId) {

        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // Update order status
    public OrderResponse updateOrderStatus(
            Long orderId, OrderStatus status) {

        if (status == null) {
            throw new IllegalArgumentException(
                    "Order status is required");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        order.setStatus(status);

        return convertToResponse(orderRepository.save(order));
    }

    // Cancel order
    public OrderResponse cancelOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        if (order.getStatus() == OrderStatus.SHIPPED
                || order.getStatus() == OrderStatus.DELIVERED) {

            throw new IllegalStateException(
                    "Shipped or delivered orders cannot be cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);

        return convertToResponse(orderRepository.save(order));
    }

    // Convert entity to response DTO
    private OrderResponse convertToResponse(Order order) {

        List<OrderItemResponse> items = order.getItems()
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getTotalPrice()))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                items);
    }
}