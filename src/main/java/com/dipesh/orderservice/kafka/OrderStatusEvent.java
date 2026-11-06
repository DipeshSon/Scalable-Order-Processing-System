package com.dipesh.orderservice.kafka;

import lombok.Data;

@Data
public class OrderStatusEvent {
    private Long orderId;
    private String status;
    private String message;
}