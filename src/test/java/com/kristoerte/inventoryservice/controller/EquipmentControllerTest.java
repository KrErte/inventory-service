package com.kristoerte.inventoryservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EquipmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getEquipmentByLocation_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/locations/LOC-1/equipment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void getEquipmentByLocation_unknownLocation_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/locations/LOC-999/equipment"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Location not found: LOC-999"));
    }

    @Test
    void getConnections_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/equipment/A/connections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getConnectedEquipment_depth2_returnsCorrectResult() throws Exception {
        mockMvc.perform(get("/api/v1/equipment/A/connected").param("depth", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].equipment.id").value("B"))
                .andExpect(jsonPath("$[0].depth").value(1));
    }

    @Test
    void getConnectedEquipment_negativeDepth_returns400() throws Exception {
        mockMvc.perform(get("/api/v1/equipment/A/connected").param("depth", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getSummary_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/inventory/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locationCount").value(2))
                .andExpect(jsonPath("$.equipmentCount").value(6));
    }
}
