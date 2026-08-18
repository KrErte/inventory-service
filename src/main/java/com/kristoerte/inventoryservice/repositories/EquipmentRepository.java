package com.kristoerte.inventoryservice.repositories;

import com.kristoerte.inventoryservice.model.Equipment;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EquipmentRepository extends com.kristoerte.inventoryservice.repository.InMemoryRepository<Equipment> {

    @Override
    protected String idOf(Equipment item) {
        return item.id();
    }

    public List<Equipment> findByLocationId(String locationId) {
        return findAll().stream()
                .filter(e -> e.locationId().equals(locationId))
                .toList();
    }
}
