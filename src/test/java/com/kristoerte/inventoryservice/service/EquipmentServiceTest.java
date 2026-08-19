package com.kristoerte.inventoryservice.service;

import com.kristoerte.inventoryservice.dto.ReachableEquipment;
import com.kristoerte.inventoryservice.exception.GlobalException;
import com.kristoerte.inventoryservice.model.Connection;
import com.kristoerte.inventoryservice.model.Equipment;
import com.kristoerte.inventoryservice.model.Location;
import com.kristoerte.inventoryservice.model.Status;
import com.kristoerte.inventoryservice.repository.ConnectionRepository;
import com.kristoerte.inventoryservice.repository.EquipmentRepository;
import com.kristoerte.inventoryservice.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EquipmentServiceTest {

    private LocationRepository locationRepository;
    private EquipmentRepository equipmentRepository;
    private ConnectionRepository connectionRepository;
    private EquipmentService service;

    @BeforeEach
    void setUp() {
        locationRepository = new LocationRepository();
        equipmentRepository = new EquipmentRepository();
        connectionRepository = new ConnectionRepository();
        service = new EquipmentService(locationRepository, equipmentRepository, connectionRepository);

        locationRepository.save(new Location("LOC-1", "Tallinn", 59.437, 24.753));
        locationRepository.save(new Location("LOC-2", "Pärnu", 58.385, 24.497));

        equipmentRepository.save(new Equipment("A", "Equipment A", "ROUTER", "LOC-1", Status.ACTIVE));
        equipmentRepository.save(new Equipment("B", "Equipment B", "SWITCH", "LOC-1", Status.ACTIVE));
        equipmentRepository.save(new Equipment("C", "Equipment C", "SWITCH", "LOC-2", Status.ACTIVE));
        equipmentRepository.save(new Equipment("D", "Equipment D", "ROUTER", "LOC-2", Status.ACTIVE));
        equipmentRepository.save(new Equipment("E", "Equipment E", "ACCESS_DEVICE", "LOC-1", Status.INACTIVE));

        connectionRepository.save(new Connection("CON-AB", "A", "B", Status.ACTIVE));
        connectionRepository.save(new Connection("CON-BC", "B", "C", Status.ACTIVE));
        connectionRepository.save(new Connection("CON-CD", "C", "D", Status.ACTIVE));
        connectionRepository.save(new Connection("CON-BE", "B", "E", Status.ACTIVE));
        connectionRepository.save(new Connection("CON-CE", "C", "E", Status.ACTIVE));
        connectionRepository.save(new Connection("CON-AE", "A", "E", Status.INACTIVE));
    }

    @Test
    void getEquipmentByLocation_returnsCorrectEquipment() {
        List<Equipment> result = service.getEquipmentByLocation("LOC-1");

        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(e -> e.locationId().equals("LOC-1")));
    }

    @Test
    void getEquipmentByLocation_unknownLocation_throwsException() {
        assertThrows(GlobalException.class, () -> service.getEquipmentByLocation("LOC-999"));
    }

    @Test
    void getConnectionsForEquipment_returnsAllConnections() {
        List<Connection> result = service.getConnectionsForEquipment("A");

        assertEquals(2, result.size());
    }

    @Test
    void getConnectionsForEquipment_unknownEquipment_throwsException() {
        assertThrows(GlobalException.class, () -> service.getConnectionsForEquipment("UNKNOWN"));
    }

    @Test
    void connectedEquipment_depth1_returnsDirectNeighbors() {
        List<ReachableEquipment> result = service.getConnectedEquipment("A", 1);

        List<String> ids = result.stream().map(r -> r.equipment().id()).toList();
        assertEquals(List.of("B"), ids);
    }

    @Test
    void connectedEquipment_depth2_returnsAssignmentExample() {
        List<ReachableEquipment> result = service.getConnectedEquipment("A", 2);

        List<String> ids = result.stream().map(r -> r.equipment().id()).toList();
        assertEquals(List.of("B", "C", "E"), ids);
    }

    @Test
    void connectedEquipment_depth3_returnsAllReachable() {
        List<ReachableEquipment> result = service.getConnectedEquipment("A", 3);

        List<String> ids = result.stream().map(r -> r.equipment().id()).toList();
        assertEquals(List.of("B", "C", "E", "D"), ids);
    }

    @Test
    void connectedEquipment_depth0_returnsEmpty() {
        List<ReachableEquipment> result = service.getConnectedEquipment("A", 0);

        assertTrue(result.isEmpty());
    }

    @Test
    void connectedEquipment_negativeDepth_throwsException() {
        assertThrows(GlobalException.class, () -> service.getConnectedEquipment("A", -1));
    }

    @Test
    void connectedEquipment_unknownEquipment_throwsException() {
        assertThrows(GlobalException.class, () -> service.getConnectedEquipment("UNKNOWN", 1));
    }

    @Test
    void connectedEquipment_isolatedEquipment_returnsEmpty() {
        equipmentRepository.save(new Equipment("ISOLATED", "Isolated", "ROUTER", "LOC-1", Status.ACTIVE));

        List<ReachableEquipment> result = service.getConnectedEquipment("ISOLATED", 5);

        assertTrue(result.isEmpty());
    }

    @Test
    void connectedEquipment_depthBeyondGraph_returnsAllReachable() {
        List<ReachableEquipment> result = service.getConnectedEquipment("A", 100);

        assertEquals(4, result.size());
    }

    @Test
    void connectedEquipment_cycleDoesNotCauseInfiniteLoop() {
        connectionRepository.save(new Connection("CON-DA", "D", "A", Status.ACTIVE));

        List<ReachableEquipment> result = service.getConnectedEquipment("A", 10);

        assertEquals(4, result.size());
    }

    @Test
    void connectedEquipment_inactiveConnectionSkipped() {
        List<ReachableEquipment> depth1 = service.getConnectedEquipment("A", 1);

        assertFalse(depth1.stream().anyMatch(r -> r.equipment().id().equals("E")));
    }

    @Test
    void connectedEquipment_correctDepthValues() {
        List<ReachableEquipment> result = service.getConnectedEquipment("A", 3);

        assertEquals(1, result.stream().filter(r -> r.equipment().id().equals("B")).findFirst().orElseThrow().depth());
        assertEquals(2, result.stream().filter(r -> r.equipment().id().equals("C")).findFirst().orElseThrow().depth());
        assertEquals(2, result.stream().filter(r -> r.equipment().id().equals("E")).findFirst().orElseThrow().depth());
        assertEquals(3, result.stream().filter(r -> r.equipment().id().equals("D")).findFirst().orElseThrow().depth());
    }
}
