package com.dipesh.orderservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(
            topics = "order-created",
            groupId = "order-service-group"
    )
    public void consumeOrderCreatedEvent(OrderEvent event) {

        System.out.println("===== ORDER CREATED EVENT RECEIVED =====");

        System.out.println("Order ID: " + event.getOrderId());
        System.out.println("User ID: " + event.getUserId());
        System.out.println("Product ID: " + event.getProductId());
        System.out.println("Quantity: " + event.getQuantity());
        System.out.println("Status: " + event.getStatus());

        System.out.println("========================================");
    }
}