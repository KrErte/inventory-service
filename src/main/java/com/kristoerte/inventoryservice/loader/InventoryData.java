package com.kristoerte.inventoryservice.loader;

import com.kristoerte.inventoryservice.model.Connection;
import com.kristoerte.inventoryservice.model.Equipment;
import com.kristoerte.inventoryservice.model.Location;

import java.util.List;

record InventoryData(List<Location> locations, List<Equipment> equipment, List<Connection> connections) {}
