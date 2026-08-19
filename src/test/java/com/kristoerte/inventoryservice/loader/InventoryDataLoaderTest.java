package com.kristoerte.inventoryservice.loader;

import com.kristoerte.inventoryservice.model.Equipment;
import com.kristoerte.inventoryservice.model.Location;
import com.kristoerte.inventoryservice.model.Status;
import com.kristoerte.inventoryservice.repository.ConnectionRepository;
import com.kristoerte.inventoryservice.repository.EquipmentRepository;
import com.kristoerte.inventoryservice.repository.LocationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class InventoryDataLoaderTest {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private ConnectionRepository connectionRepository;

    @Test
    void allLocationsLoaded() {
        assertEquals(2, locationRepository.count());
        assertTrue(locationRepository.existsById("LOC-1"));
        assertTrue(locationRepository.existsById("LOC-2"));
    }

    @Test
    void allEquipmentLoaded_includingOrphanedLocation() {
        assertEquals(6, equipmentRepository.count());
        assertTrue(equipmentRepository.existsById("F"));
    }

    @Test
    void equipmentWithInvalidLocation_stillLoaded() {
        Equipment f = equipmentRepository.findById("F").orElseThrow();
        assertEquals("LOC-999", f.locationId());
        assertFalse(locationRepository.existsById("LOC-999"));
    }

    @Test
    void allConnectionsLoaded_includingBroken() {
        assertEquals(7, connectionRepository.count());
        assertTrue(connectionRepository.existsById("CON-BROKEN"));
    }

    @Test
    void brokenConnection_targetDoesNotExist() {
        var broken = connectionRepository.findById("CON-BROKEN").orElseThrow();
        assertEquals("DOES-NOT-EXIST", broken.targetEquipmentId());
        assertFalse(equipmentRepository.existsById("DOES-NOT-EXIST"));
    }

    @Test
    void duplicateIds_firstEntryWins() {
        Location loc1 = locationRepository.findById("LOC-1").orElseThrow();
        assertEquals("Tallinn", loc1.name());
    }

    @Test
    void inactiveEquipment_loaded() {
        Equipment e = equipmentRepository.findById("E").orElseThrow();
        assertEquals(Status.INACTIVE, e.status());
    }
}
