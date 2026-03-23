package com.sts.market;

import com.sts.shared.model.CartEntry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CartEntryTest {

    @Test
    void shouldCreateCartEntryWithCorrectValues() {
        CartEntry entry = new CartEntry("u1", "P001", 3);

        assertEquals("u1", entry.getUserId());
        assertEquals("P001", entry.getProductId());
        assertEquals(3, entry.getQuantity());
    }

    @Test
    void shouldUpdateQuantity() {
        CartEntry entry = new CartEntry("u1", "P001", 3);
        entry.setQuantity(5);
        assertEquals(5, entry.getQuantity());
    }

    @Test
    void shouldAllowQuantityOfOne() {
        CartEntry entry = new CartEntry("u1", "P001", 1);
        assertEquals(1, entry.getQuantity());
    }
}
