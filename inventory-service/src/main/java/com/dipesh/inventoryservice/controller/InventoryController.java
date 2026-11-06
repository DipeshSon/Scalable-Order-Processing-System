package com.dipesh.inventoryservice.controller;

import com.dipesh.inventoryservice.entity.Inventory;
import com.dipesh.inventoryservice.service.InventoryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public Inventory addInventory(@RequestBody Inventory inventory) {
        return inventoryService.addInventory(inventory);
    }

    @GetMapping("/{productId}")
    public Inventory getInventory(@PathVariable Long productId) {
        return inventoryService.getInventoryByProductId(productId);
    }

    @PostMapping("/reduce")
    public String reduceStock(
            @RequestParam Long productId,
            @RequestParam Integer quantity) {

        inventoryService.reduceStock(productId, quantity);

        return "Stock reduced successfully";
    }
}