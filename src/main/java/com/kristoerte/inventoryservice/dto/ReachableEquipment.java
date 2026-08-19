package com.kristoerte.inventoryservice.dto;

import com.kristoerte.inventoryservice.model.Equipment;

public record ReachableEquipment(Equipment equipment, int depth) {
}
