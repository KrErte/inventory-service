package com.kristoerte.inventoryservice.loader;

import tools.jackson.databind.ObjectMapper;
import com.kristoerte.inventoryservice.model.Connection;
import com.kristoerte.inventoryservice.model.Equipment;
import com.kristoerte.inventoryservice.model.Location;
import com.kristoerte.inventoryservice.repository.ConnectionRepository;
import com.kristoerte.inventoryservice.repository.EquipmentRepository;
import com.kristoerte.inventoryservice.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InventoryDataLoader implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(InventoryDataLoader.class);

    private final LocationRepository locationRepository;
    private final EquipmentRepository equipmentRepository;
    private final ConnectionRepository connectionRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        InventoryData data;
        try (InputStream in = new ClassPathResource("data/inventory.json").getInputStream()) {
            data = objectMapper.readValue(in, InventoryData.class);
        }

        loadLocations(data.locations());
        loadEquipment(data.equipment());
        loadConnections(data.connections());

        log.info("Inventory loaded: {} locations, {} equipment, {} connections",
                locationRepository.count(), equipmentRepository.count(), connectionRepository.count());
    }

    private void loadLocations(List<Location> locations) {
        for (Location location : locations) {
            if (!locationRepository.save(location)) {
                log.warn("Skipping duplicate location id: {}", location.id());
            }
        }
    }

    private void loadEquipment(List<Equipment> items) {
        for (Equipment item : items) {
            if (!equipmentRepository.save(item)) {
                log.warn("Skipping duplicate equipment id: {}", item.id());
                continue;
            }
            if (!locationRepository.existsById(item.locationId())) {
                log.warn("Equipment {} references unknown location {}", item.id(), item.locationId());
            }
        }
    }

    private void loadConnections(List<Connection> connections) {
        for (Connection connection : connections) {
            if (!connectionRepository.save(connection)) {
                log.warn("Skipping duplicate connection id: {}", connection.id());
                continue;
            }
            if (!equipmentRepository.existsById(connection.sourceEquipmentId())) {
                log.warn("Connection {} references unknown source equipment {}",
                        connection.id(), connection.sourceEquipmentId());
            }
            if (!equipmentRepository.existsById(connection.targetEquipmentId())) {
                log.warn("Connection {} references unknown target equipment {}",
                        connection.id(), connection.targetEquipmentId());
            }
        }
    }
}
