package com.kristoerte.inventoryservice.model;

import java.util.Objects;

public record Equipment(String id, String name, String type, String locationId, Status status) {

    public Equipment {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(status, "status must not be null");
    }
}
