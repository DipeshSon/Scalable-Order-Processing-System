package com.dipesh.orderservice.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {

    private Long orderId;

    private Long userId;

    private Long productId;

    private Integer quantity;

    private String status;
}