package com.kristoerte.inventoryservice.dto;

public record InventorySummary(
        int locationCount,
        int equipmentCount,
        int activeConnectionCount,
        int inactiveConnectionCount) {
}
