package com.dipesh.inventoryservice.kafka;

import com.dipesh.inventoryservice.service.InventoryService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private final InventoryService inventoryService;
    private final KafkaProducerService kafkaProducerService;

    public KafkaConsumerService(
            InventoryService inventoryService,
            KafkaProducerService kafkaProducerService) {

        this.inventoryService = inventoryService;
        this.kafkaProducerService = kafkaProducerService;
    }

    @KafkaListener(
            topics = "order-created",
            groupId = "inventory-service-group"
    )
    public void consumeOrderCreatedEvent(OrderEvent event) {

        System.out.println("===== INVENTORY SERVICE RECEIVED ORDER =====");

        System.out.println("Order ID: " + event.getOrderId());
        System.out.println("Product ID: " + event.getProductId());
        System.out.println("Quantity: " + event.getQuantity());

        try {

            // Reduce stock
            boolean processed = inventoryService.processOrder(
                    event.getOrderId(),
                    event.getProductId(),
                    event.getQuantity()
            );

            if (!processed) {
                System.out.println("Duplicate order ignored: " + event.getOrderId());
                return;
            }
            // Create success event
            OrderStatusEvent statusEvent = new OrderStatusEvent();

            statusEvent.setOrderId(event.getOrderId());
            statusEvent.setStatus("CONFIRMED");
            statusEvent.setMessage("Stock reduced successfully");

            // Send response to Kafka
            kafkaProducerService.sendOrderStatus(statusEvent);

            System.out.println("Order CONFIRMED");

        } catch (Exception e) {

            // Create failure event
            OrderStatusEvent statusEvent = new OrderStatusEvent();

            statusEvent.setOrderId(event.getOrderId());
            statusEvent.setStatus("FAILED");
            statusEvent.setMessage(e.getMessage());

            // Send failure response to Kafka
            kafkaProducerService.sendOrderStatus(statusEvent);

            System.out.println("Order FAILED");
            System.out.println("Reason: " + e.getMessage());
        }

        System.out.println("============================================");
    }
}