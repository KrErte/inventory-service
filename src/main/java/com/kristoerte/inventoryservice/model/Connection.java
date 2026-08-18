package com.kristoerte.inventoryservice.model;

import java.util.Objects;

public record Connection(String id, String sourceEquipmentId, String targetEquipmentId, Status status) {

    public Connection {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(sourceEquipmentId, "sourceEquipmentId must not be null");
        Objects.requireNonNull(targetEquipmentId, "targetEquipmentId must not be null");
        Objects.requireNonNull(status, "status must not be null");
    }
}
