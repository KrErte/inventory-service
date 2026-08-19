package com.kristoerte.inventoryservice.controller;

import com.kristoerte.inventoryservice.dto.InventorySummary;
import com.kristoerte.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/summary")
    public InventorySummary getSummary() {
        return inventoryService.getSummary();
    }
}
