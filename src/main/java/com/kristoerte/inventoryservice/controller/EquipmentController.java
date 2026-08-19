package com.kristoerte.inventoryservice.controller;

import com.kristoerte.inventoryservice.dto.ReachableEquipment;
import com.kristoerte.inventoryservice.model.Connection;
import com.kristoerte.inventoryservice.model.Equipment;
import com.kristoerte.inventoryservice.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @GetMapping("/locations/{locationId}/equipment")
    public List<Equipment> getEquipmentByLocation(@PathVariable String locationId) {
        return equipmentService.getEquipmentByLocation(locationId);
    }

    @GetMapping("/equipment/{equipmentId}/connections")
    public List<Connection> getConnectionsForEquipment(@PathVariable String equipmentId) {
        return equipmentService.getConnectionsForEquipment(equipmentId);
    }

    @GetMapping("/equipment/{equipmentId}/connected")
    public List<ReachableEquipment> getConnectedEquipment(
            @PathVariable String equipmentId,
            @RequestParam(defaultValue = "1") int depth) {
        return equipmentService.getConnectedEquipment(equipmentId, depth);
    }
}
