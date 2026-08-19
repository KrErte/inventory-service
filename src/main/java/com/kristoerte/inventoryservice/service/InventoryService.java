package com.kristoerte.inventoryservice.service;

import com.kristoerte.inventoryservice.dto.InventorySummary;
import com.kristoerte.inventoryservice.model.Status;
import com.kristoerte.inventoryservice.repository.ConnectionRepository;
import com.kristoerte.inventoryservice.repository.EquipmentRepository;
import com.kristoerte.inventoryservice.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final LocationRepository locationRepository;
    private final EquipmentRepository equipmentRepository;
    private final ConnectionRepository connectionRepository;

    public InventorySummary getSummary() {
        long activeConnections = connectionRepository.findAll().stream()
                .filter(connection -> connection.status() == Status.ACTIVE)
                .count();
        long inactiveConnections = connectionRepository.count() - activeConnections;

        return new InventorySummary(
                locationRepository.count(),
                equipmentRepository.count(),
                (int) activeConnections,
                (int) inactiveConnections);
    }
}
