package com.dipesh.inventoryservice.service;

import com.dipesh.inventoryservice.entity.Inventory;
import com.dipesh.inventoryservice.repository.InventoryRepository;
import com.dipesh.inventoryservice.exception.ProductNotFoundException;
import com.dipesh.inventoryservice.exception.InsufficientStockException;
import com.dipesh.inventoryservice.entity.ProcessedOrder;
import com.dipesh.inventoryservice.Repository.ProcessedOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import org.springframework.data.redis.core.RedisTemplate;




@Service
public class InventoryService {

    private final ProcessedOrderRepository processedOrderRepository;

    private final InventoryRepository inventoryRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public InventoryService(
            InventoryRepository inventoryRepository,
            RedisTemplate<String, String> redisTemplate,
            ProcessedOrderRepository processedOrderRepository) {

        this.inventoryRepository = inventoryRepository;
        this.redisTemplate = redisTemplate;
        this.processedOrderRepository = processedOrderRepository;
    }

    public Inventory addInventory(Inventory inventory) {

        Inventory savedInventory =
                inventoryRepository.save(inventory);

        String key = "inventory:" + savedInventory.getProductId();

        redisTemplate.opsForHash().put(
                key,
                "id",
                savedInventory.getId().toString()
        );

        redisTemplate.opsForHash().put(
                key,
                "productId",
                savedInventory.getProductId().toString()
        );

        redisTemplate.opsForHash().put(
                key,
                "quantity",
                savedInventory.getQuantity().toString()
        );

        return savedInventory;
    }

    public Inventory getInventoryByProductId(Long productId) {

        String key = "inventory:" + productId;

        // 1. Check Redis
        Object cachedProductId =
                redisTemplate.opsForHash().get(key, "productId");

        if (cachedProductId != null) {

            System.out.println("===== REDIS CACHE HIT =====");
            System.out.println("Product ID: " + productId);
            System.out.println("==========================");

            Inventory inventory = new Inventory();

            inventory.setId(
                    Long.valueOf(
                            redisTemplate.opsForHash()
                                    .get(key, "id")
                                    .toString()
                    )
            );

            inventory.setProductId(
                    Long.valueOf(
                            redisTemplate.opsForHash()
                                    .get(key, "productId")
                                    .toString()
                    )
            );

            inventory.setQuantity(
                    Integer.valueOf(
                            redisTemplate.opsForHash()
                                    .get(key, "quantity")
                                    .toString()
                    )
            );

            return inventory;
        }

        // 2. Redis MISS → MySQL
        System.out.println("===== REDIS CACHE MISS =====");
        System.out.println("Fetching from MySQL...");
        System.out.println("============================");

        Inventory inventory =
                inventoryRepository.findByProductId(productId)
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product not found: " + productId
                                )
                        );

        // 3. Store in Redis
        redisTemplate.opsForHash().put(
                key,
                "id",
                inventory.getId().toString()
        );

        redisTemplate.opsForHash().put(
                key,
                "productId",
                inventory.getProductId().toString()
        );

        redisTemplate.opsForHash().put(
                key,
                "quantity",
                inventory.getQuantity().toString()
        );

        System.out.println("Inventory stored in Redis.");

        return inventory;
    }

    public void reduceStock(Long productId, Integer quantity) {

        Inventory inventory =
                inventoryRepository.findByProductId(productId)
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product not found: " + productId
                                )
                        );

        if (inventory.getQuantity() < quantity) {

            throw new InsufficientStockException(
                    "Insufficient stock for product: " + productId
            );
        }

        inventory.setQuantity(
                inventory.getQuantity() - quantity
        );

        Inventory updatedInventory =
                inventoryRepository.save(inventory);

        // Update Redis
        String key = "inventory:" + productId;

        redisTemplate.opsForHash().put(
                key,
                "id",
                updatedInventory.getId().toString()
        );

        redisTemplate.opsForHash().put(
                key,
                "productId",
                updatedInventory.getProductId().toString()
        );

        redisTemplate.opsForHash().put(
                key,
                "quantity",
                updatedInventory.getQuantity().toString()
        );

        System.out.println(
                "Redis cache updated after stock reduction."
        );
    }


    @Transactional
    public boolean processOrder(
            Long orderId,
            Long productId,
            Integer quantity) {

        // Has this order already been processed?
        if (processedOrderRepository.existsById(orderId)) {
            System.out.println(
                    "Duplicate order ignored: " + orderId
            );
            return false;
        }

        // Reduce stock using your existing method
        reduceStock(productId, quantity);

        // Remember the successfully processed order
        processedOrderRepository.saveAndFlush(
                new ProcessedOrder(orderId)
        );

        return true;
    }
}