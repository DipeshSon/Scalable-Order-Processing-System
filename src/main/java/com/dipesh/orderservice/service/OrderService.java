package com.dipesh.orderservice.service;

import com.dipesh.orderservice.dto.OrderRequest;
import com.dipesh.orderservice.dto.OrderResponse;
import com.dipesh.orderservice.entity.Order;
import com.dipesh.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import com.dipesh.orderservice.kafka.OrderEvent;

import java.time.LocalDateTime;
import java.util.List;
import com.dipesh.orderservice.kafka.KafkaProducerService;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final KafkaProducerService kafkaProducerService;

    public OrderService(OrderRepository orderRepository, KafkaProducerService kafkaProducerService) {
        this.orderRepository = orderRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    public OrderResponse createOrder(OrderRequest request) {

        Order order = new Order();

        order.setUserId(request.getUserId());
        order.setProductId(request.getProductId());
        order.setQuantity(request.getQuantity());

        order.setStatus("CREATED");
        order.setCreatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        OrderEvent event = new OrderEvent(
                savedOrder.getId(),
                savedOrder.getUserId(),
                savedOrder.getProductId(),
                savedOrder.getQuantity(),
                savedOrder.getStatus()
        );

        kafkaProducerService.sendOrderCreatedEvent(event);

        return new OrderResponse(
                savedOrder.getId(),
                savedOrder.getUserId(),
                savedOrder.getProductId(),
                savedOrder.getQuantity(),
                savedOrder.getStatus(),
                savedOrder.getCreatedAt()
        );
    }

    public OrderResponse getOrderById(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Order not found with id: " + id));

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getProductId(),
                order.getQuantity(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }

    public List<OrderResponse> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(order -> new OrderResponse(
                        order.getId(),
                        order.getUserId(),
                        order.getProductId(),
                        order.getQuantity(),
                        order.getStatus(),
                        order.getCreatedAt()
                ))
                .toList();
    }
}