package com.dipesh.inventoryservice.kafka;

import lombok.Data;

@Data
public class OrderStatusEvent {

    private Long orderId;
    private String status;
    private String message;
}