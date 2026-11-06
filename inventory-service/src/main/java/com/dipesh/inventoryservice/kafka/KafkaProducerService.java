package com.dipesh.inventoryservice.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final String TOPIC = "order-status";

    private final KafkaTemplate<String, OrderStatusEvent> kafkaTemplate;

    public KafkaProducerService(
            KafkaTemplate<String, OrderStatusEvent> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderStatus(OrderStatusEvent event) {

        kafkaTemplate.send(
                TOPIC,
                event.getOrderId().toString(),
                event
        );

        System.out.println(
                "Order status event sent: " + event.getStatus()
        );
    }
}