package com.kristoerte.inventoryservice.service;

import com.kristoerte.inventoryservice.dto.ReachableEquipment;
import com.kristoerte.inventoryservice.exception.GlobalException;
import com.kristoerte.inventoryservice.model.Connection;
import org.springframework.http.HttpStatus;
import com.kristoerte.inventoryservice.model.Equipment;
import com.kristoerte.inventoryservice.model.Status;
import com.kristoerte.inventoryservice.repository.ConnectionRepository;
import com.kristoerte.inventoryservice.repository.EquipmentRepository;
import com.kristoerte.inventoryservice.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final LocationRepository locationRepository;
    private final EquipmentRepository equipmentRepository;
    private final ConnectionRepository connectionRepository;

    public List<Equipment> getEquipmentByLocation(String locationId) {
        if (!locationRepository.existsById(locationId)) {
            throw new GlobalException("Location", locationId, HttpStatus.NOT_FOUND);
        }
        return equipmentRepository.findByLocationId(locationId);
    }

    public List<Connection> getConnectionsForEquipment(String equipmentId) {
        if (!equipmentRepository.existsById(equipmentId)) {
            throw new GlobalException("Equipment", equipmentId, HttpStatus.NOT_FOUND);
        }
        return connectionRepository.findByEquipmentId(equipmentId);
    }

    public List<ReachableEquipment> getConnectedEquipment(String equipmentId, int maxDepth) {
        if (!equipmentRepository.existsById(equipmentId)) {
            throw new GlobalException("Equipment", equipmentId, HttpStatus.NOT_FOUND);
        }
        if (maxDepth < 0) {
            throw new GlobalException("Depth must not be negative", HttpStatus.BAD_REQUEST);
        }

        Map<String, Integer> depthById = new LinkedHashMap<>();
        depthById.put(equipmentId, 0);

        Queue<String> queue = new ArrayDeque<>();
        queue.add(equipmentId);

        while (!queue.isEmpty()) {
            String currentId = queue.poll();
            int currentDepth = depthById.get(currentId);
            if (currentDepth == maxDepth) {
                continue;
            }
            for (String neighborId : activeNeighborsOf(currentId)) {
                if (!depthById.containsKey(neighborId)) {
                    depthById.put(neighborId, currentDepth + 1);
                    queue.add(neighborId);
                }
            }
        }

        depthById.remove(equipmentId);

        return depthById.entrySet().stream()
                .map(entry -> new ReachableEquipment(
                        equipmentRepository.findById(entry.getKey()).orElseThrow(),
                        entry.getValue()))
                .sorted(Comparator.comparingInt(ReachableEquipment::depth)
                        .thenComparing(r -> r.equipment().id()))
                .toList();
    }

    private List<String> activeNeighborsOf(String equipmentId) {
        return connectionRepository.findByEquipmentId(equipmentId).stream()
                .filter(connection -> connection.status() == Status.ACTIVE)
                .map(connection -> connection.sourceEquipmentId().equals(equipmentId)
                        ? connection.targetEquipmentId()
                        : connection.sourceEquipmentId())
                .filter(equipmentRepository::existsById)
                .toList();
    }
}
