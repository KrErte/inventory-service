package com.kristoerte.inventoryservice.repositories;

import com.kristoerte.inventoryservice.model.Connection;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ConnectionRepository extends com.kristoerte.inventoryservice.repository.InMemoryRepository<Connection> {

    @Override
    protected String idOf(Connection item) {
        return item.id();
    }

    public List<Connection> findByEquipmentId(String equipmentId) {
        return findAll().stream()
                .filter(c -> c.sourceEquipmentId().equals(equipmentId)
                        || c.targetEquipmentId().equals(equipmentId))
                .toList();
    }
}
