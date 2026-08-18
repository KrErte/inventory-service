package com.kristoerte.inventoryservice.repository;

import com.kristoerte.inventoryservice.model.Location;
import org.springframework.stereotype.Repository;

@Repository
public class LocationRepository extends InMemoryRepository<Location> {

    @Override
    protected String idOf(Location item) {
        return item.id();
    }
}
