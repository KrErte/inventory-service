package com.kristoerte.inventoryservice.service;

import com.kristoerte.inventoryservice.dto.InventorySummary;
import com.kristoerte.inventoryservice.model.Connection;
import com.kristoerte.inventoryservice.model.Equipment;
import com.kristoerte.inventoryservice.model.Location;
import com.kristoerte.inventoryservice.model.Status;
import com.kristoerte.inventoryservice.repository.ConnectionRepository;
import com.kristoerte.inventoryservice.repository.EquipmentRepository;
import com.kristoerte.inventoryservice.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryServiceTest {

    private InventoryService service;

    @BeforeEach
    void setUp() {
        LocationRepository locationRepository = new LocationRepository();
        EquipmentRepository equipmentRepository = new EquipmentRepository();
        ConnectionRepository connectionRepository = new ConnectionRepository();
        service = new InventoryService(locationRepository, equipmentRepository, connectionRepository);

        locationRepository.save(new Location("LOC-1", "Tallinn", 59.437, 24.753));
        locationRepository.save(new Location("LOC-2", "Pärnu", 58.385, 24.497));

        equipmentRepository.save(new Equipment("A", "Equipment A", "ROUTER", "LOC-1", Status.ACTIVE));
        equipmentRepository.save(new Equipment("B", "Equipment B", "SWITCH", "LOC-1", Status.ACTIVE));

        connectionRepository.save(new Connection("CON-1", "A", "B", Status.ACTIVE));
        connectionRepository.save(new Connection("CON-2", "A", "B", Status.INACTIVE));
        connectionRepository.save(new Connection("CON-3", "A", "B", Status.ACTIVE));
    }

    @Test
    void getSummary_returnsCorrectCounts() {
        InventorySummary summary = service.getSummary();

        assertEquals(2, summary.locationCount());
        assertEquals(2, summary.equipmentCount());
        assertEquals(2, summary.activeConnectionCount());
        assertEquals(1, summary.inactiveConnectionCount());
    }

    @Test
    void getSummary_emptyInventory() {
        InventoryService emptyService = new InventoryService(
                new LocationRepository(), new EquipmentRepository(), new ConnectionRepository());

        InventorySummary summary = emptyService.getSummary();

        assertEquals(0, summary.locationCount());
        assertEquals(0, summary.equipmentCount());
        assertEquals(0, summary.activeConnectionCount());
        assertEquals(0, summary.inactiveConnectionCount());
    }
}
