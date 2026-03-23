package com.sts.market;

import com.sts.shared.model.Product;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void shouldCreateProductWithCorrectValues() {
        Product product = new Product("P001", "Leche", 1.20, 50, 100);

        assertEquals("P001", product.getProductId());
        assertEquals("Leche", product.getName());
        assertEquals(1.20, product.getPrice());
        assertEquals(50, product.getCurrentStock());
        assertEquals(100, product.getMaxCapacity());
    }

    @Test
    void shouldUpdateCurrentStock() {
        Product product = new Product("P001", "Leche", 1.20, 50, 100);
        product.setCurrentStock(30);
        assertEquals(30, product.getCurrentStock());
    }

    @Test
    void shouldAllowZeroStock() {
        Product product = new Product("P001", "Leche", 1.20, 0, 100);
        assertEquals(0, product.getCurrentStock());
    }

    @Test
    void shouldAllowStockEqualToMaxCapacity() {
        Product product = new Product("P001", "Leche", 1.20, 100, 100);
        assertEquals(100, product.getCurrentStock());
    }
}
