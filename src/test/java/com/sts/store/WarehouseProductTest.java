package com.sts.store;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WarehouseProductTest {

    @Test
    void shouldCreateWithCorrectValues() {
        WarehouseProduct wp = new WarehouseProduct("P001", "Leche", 100);
        assertEquals("P001", wp.getProductId());
        assertEquals("Leche", wp.getName());
        assertEquals(100, wp.getTotalStock());
    }

    @Test
    void shouldUpdateTotalStock() {
        WarehouseProduct wp = new WarehouseProduct("P001", "Leche", 100);
        wp.setTotalStock(50);
        assertEquals(50, wp.getTotalStock());
    }
    @Test
    void shouldUpdateProductId() {
        WarehouseProduct wp = new WarehouseProduct("P001", "Leche", 100);
        wp.setProductId("P002");
        assertEquals("P002", wp.getProductId());
    }

    @Test
    void shouldUpdateName() {
        WarehouseProduct wp = new WarehouseProduct("P001", "Leche", 100);
        wp.setName("Pan");
        assertEquals("Pan", wp.getName());
    }

    @Test
    void shouldFormatToStringCorrectly() {
        WarehouseProduct wp = new WarehouseProduct("P001", "Leche", 100);
        assertEquals("P001,Leche,100", wp.toString());
    }
}
