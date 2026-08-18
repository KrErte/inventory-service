package com.kristoerte.inventoryservice.repositories;

import com.kristoerte.inventoryservice.model.Location;
import org.springframework.stereotype.Repository;

@Repository
public class LocationRepository extends com.kristoerte.inventoryservice.repository.InMemoryRepository<Location> {

    @Override
    protected String idOf(Location item) {
        return item.id();
    }
}
