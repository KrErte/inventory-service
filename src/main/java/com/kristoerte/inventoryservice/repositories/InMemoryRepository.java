package com.kristoerte.inventoryservice.repository;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public abstract class InMemoryRepository<T> {

    private final Map<String, T> store = new LinkedHashMap<>();

    protected abstract String idOf(T item);

    public boolean save(T item) {
        String id = idOf(item);
        if (store.containsKey(id)) {
            return false;
        }
        store.put(id, item);
        return true;
    }

    public Optional<T> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public boolean existsById(String id) {
        return store.containsKey(id);
    }

    public Collection<T> findAll() {
        return store.values();
    }

    public int count() {
        return store.size();
    }
}
