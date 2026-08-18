package com.kristoerte.inventoryservice.model;

import java.util.Objects;

public record Location(String id, String name, double latitude, double longitude) {

    public Location {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
    }
}
