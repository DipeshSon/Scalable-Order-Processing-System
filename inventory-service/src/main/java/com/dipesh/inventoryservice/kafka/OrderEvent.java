package com.dipesh.inventoryservice.kafka;

import lombok.Data;

@Data
public class OrderEvent {

    private Long orderId;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private String status;
}