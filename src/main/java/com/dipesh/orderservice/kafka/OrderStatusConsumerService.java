package com.dipesh.orderservice.kafka;

import com.dipesh.orderservice.entity.Order;
import com.dipesh.orderservice.repository.OrderRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderStatusConsumerService {

    private final OrderRepository orderRepository;

    public OrderStatusConsumerService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @KafkaListener(
            topics = "order-status",
            groupId = "order-service-status-group"
    )
    public void consumeOrderStatus(OrderStatusEvent event) {

        System.out.println("===== ORDER STATUS EVENT RECEIVED =====");
        System.out.println("Order ID: " + event.getOrderId());
        System.out.println("Status: " + event.getStatus());
        System.out.println("Message: " + event.getMessage());

        Order order = orderRepository.findById(event.getOrderId())
                .orElse(null);

        if (order == null) {
            System.out.println("Order not found: " + event.getOrderId());
            return;
        }

        order.setStatus(event.getStatus());
        orderRepository.save(order);

        System.out.println("Order status updated to: " + order.getStatus());
        System.out.println("=======================================");
    }
}