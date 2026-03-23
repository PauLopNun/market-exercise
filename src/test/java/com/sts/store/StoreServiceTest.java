package com.sts.store;

import com.sts.shared.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StoreServiceTest {

    @TempDir
    Path tempDir;

    private Path stockFile;
    private Path warehouseFile;
    private StoreService service;

    @BeforeEach
    void setUp() throws IOException {
        stockFile = tempDir.resolve("market_stock.csv");
        warehouseFile = tempDir.resolve("warehouse.csv");

        Files.writeString(stockFile,
                "productId,name,price,current_stock,max_capacity\n" +
                "P001,Leche,1.20,10,100\n" +
                "P002,Pan,0.80,50,100\n"
        );
        Files.writeString(warehouseFile,
                "productId,name,total_stock\n" +
                "P001,Leche,200\n" +
                "P002,Pan,200\n"
        );

        service = new StoreService(stockFile.toString(), warehouseFile.toString());
    }

    @Test
    void shouldReadMarketStock() throws IOException {
        List<Product> products = service.readMarketStock();
        assertEquals(2, products.size());
        assertEquals("P001", products.get(0).getProductId());
    }

    @Test
    void shouldReadWarehouse() throws IOException {
        List<WarehouseProduct> products = service.readWarehouse();
        assertEquals(2, products.size());
        assertEquals("P001", products.get(0).getProductId());
    }

    @Test
    void shouldReplenishWhenBelowThreshold() throws IOException {
        List<Product> market = service.readMarketStock();
        List<WarehouseProduct> warehouse = service.readWarehouse();

        // P001 has 10/100 = 10% < 20% threshold
        boolean modified = service.replenishLowStockProducts(market, warehouse);

        assertTrue(modified);
        assertEquals(100, market.get(0).getCurrentStock());
        assertEquals(110, warehouse.get(0).getTotalStock()); // 200 - 90
    }

    @Test
    void shouldNotReplenishWhenAboveThreshold() throws IOException {
        List<Product> market = service.readMarketStock();
        List<WarehouseProduct> warehouse = service.readWarehouse();

        // P002 has 50/100 = 50% > 20% threshold
        market.remove(0); // only keep P002
        boolean modified = service.replenishLowStockProducts(market, warehouse);

        assertFalse(modified);
    }

    @Test
    void shouldNotReplenishWhenWarehouseEmpty() throws IOException {
        Files.writeString(warehouseFile,
                "productId,name,total_stock\n" +
                "P001,Leche,0\n"
        );
        service = new StoreService(stockFile.toString(), warehouseFile.toString());
        List<Product> market = service.readMarketStock();
        List<WarehouseProduct> warehouse = service.readWarehouse();

        boolean modified = service.replenishLowStockProducts(market, warehouse);
        assertFalse(modified);
    }

    @Test
    void shouldWriteMarketStock() throws IOException {
        List<Product> products = service.readMarketStock();
        products.get(0).setCurrentStock(99);
        service.writeMarketStock(products);

        List<Product> reloaded = service.readMarketStock();
        assertEquals(99, reloaded.get(0).getCurrentStock());
    }

    @Test
    void shouldWriteWarehouse() throws IOException {
        List<WarehouseProduct> products = service.readWarehouse();
        products.get(0).setTotalStock(150);
        service.writeWarehouse(products);

        List<WarehouseProduct> reloaded = service.readWarehouse();
        assertEquals(150, reloaded.get(0).getTotalStock());
    }

    @Test
    void shouldRefillAndPersistChanges() throws IOException {
        service.refillProducts();

        List<Product> market = service.readMarketStock();
        assertEquals(100, market.get(0).getCurrentStock());

        List<WarehouseProduct> warehouse = service.readWarehouse();
        assertEquals(110, warehouse.get(0).getTotalStock());
    }

    @Test
    void shouldNotWriteWhenNothingReplenished() throws IOException {
        // P001 at 10% triggers replenish, but let's set both above threshold
        Files.writeString(stockFile,
                "productId,name,price,current_stock,max_capacity\n" +
                "P001,Leche,1.20,50,100\n" +
                "P002,Pan,0.80,50,100\n"
        );
        service = new StoreService(stockFile.toString(), warehouseFile.toString());
        service.refillProducts();

        // stock should remain unchanged
        List<Product> market = service.readMarketStock();
        assertEquals(50, market.get(0).getCurrentStock());
    }

    @Test
    void shouldIgnoreBlankLinesInStockCsv() throws IOException {
        Files.writeString(stockFile,
                "productId,name,price,current_stock,max_capacity\n" +
                "\n" +
                "P001,Leche,1.20,10,100\n"
        );
        List<Product> products = service.readMarketStock();
        assertEquals(1, products.size());
    }

    @Test
    void shouldIgnoreBlankLinesInWarehouseCsv() throws IOException {
        Files.writeString(warehouseFile,
                "productId,name,total_stock\n" +
                "\n" +
                "P001,Leche,200\n"
        );
        List<WarehouseProduct> products = service.readWarehouse();
        assertEquals(1, products.size());
    }

    @Test
    void shouldThrowIOExceptionWhenStockFileNotFound() {
        StoreService bad = new StoreService("nonexistent.csv", warehouseFile.toString());
        assertThrows(IOException.class, bad::readMarketStock);
    }

    @Test
    void shouldThrowIOExceptionWhenWarehouseFileNotFound() {
        StoreService bad = new StoreService(stockFile.toString(), "nonexistent.csv");
        assertThrows(IOException.class, bad::readWarehouse);
    }

    @Test
    void shouldReplenishToExactMaxCapacity() throws IOException {
        // P001 at 10, max 100, warehouse has 200
        List<Product> market = service.readMarketStock();
        List<WarehouseProduct> warehouse = service.readWarehouse();

        // Needed = 100 - 10 = 90
        service.replenishLowStockProducts(market, warehouse);

        assertEquals(100, market.get(0).getCurrentStock());
        assertEquals(110, warehouse.get(0).getTotalStock()); // 200 - 90
    }

    @Test
    void shouldTransferOnlyAvailableWarehouseStock() throws IOException {
        // Set warehouse to have limited stock
        Files.writeString(warehouseFile,
                "productId,name,total_stock\n" +
                "P001,Leche,50\n" +
                "P002,Pan,200\n"
        );
        service = new StoreService(stockFile.toString(), warehouseFile.toString());

        List<Product> market = service.readMarketStock();
        List<WarehouseProduct> warehouse = service.readWarehouse();

        // P001: needs 90, warehouse has 50 — transfer only 50
        service.replenishLowStockProducts(market, warehouse);

        assertEquals(60, market.get(0).getCurrentStock()); // 10 + 50
        assertEquals(0, warehouse.get(0).getTotalStock()); // 50 - 50
    }

    @Test
    void shouldHandleMultipleProductReplenishment() throws IOException {
        Files.writeString(stockFile,
                "productId,name,price,current_stock,max_capacity\n" +
                "P001,Leche,1.20,5,100\n" +
                "P002,Pan,0.80,8,100\n"
        );
        service = new StoreService(stockFile.toString(), warehouseFile.toString());

        List<Product> market = service.readMarketStock();
        List<WarehouseProduct> warehouse = service.readWarehouse();

        // Both below 20% threshold
        boolean modified = service.replenishLowStockProducts(market, warehouse);

        assertTrue(modified);
        assertEquals(100, market.get(0).getCurrentStock());
    }

    @Test
    void refillProducts_shouldNotModifyWhenAllAboveThreshold() throws IOException {
        // All products above 20% threshold
        Files.writeString(stockFile,
                "productId,name,price,current_stock,max_capacity\n" +
                "P001,Leche,1.20,50,100\n" +
                "P002,Pan,0.80,50,100\n"
        );
        service = new StoreService(stockFile.toString(), warehouseFile.toString());
        
        // First read to get initial state
        List<Product> before = service.readMarketStock();
        assertEquals(50, before.get(0).getCurrentStock());
        
        // Call refillProducts - should not modify anything
        service.refillProducts();
        
        // Verify no changes
        List<Product> after = service.readMarketStock();
        assertEquals(50, after.get(0).getCurrentStock());
    }

    @Test
    void writeMarketStockWithMultipleProducts() throws IOException {
        List<Product> products = service.readMarketStock();
        products.get(0).setCurrentStock(75);
        products.get(1).setCurrentStock(85);
        service.writeMarketStock(products);

        List<Product> reloaded = service.readMarketStock();
        assertEquals(75, reloaded.get(0).getCurrentStock());
        assertEquals(85, reloaded.get(1).getCurrentStock());
    }

    @Test
    void refillProducts_CompleteFlow() throws IOException {
        service.refillProducts();
        List<Product> market = service.readMarketStock();
        assertEquals(100, market.get(0).getCurrentStock());
    }

    @Test
    void shouldReplenishWhenExactlyAtThreshold() throws IOException {
        Files.writeString(stockFile,
                "productId,name,price,current_stock,max_capacity\n" +
                "P001,Leche,1.20,20,100\n"
        );
        service = new StoreService(stockFile.toString(), warehouseFile.toString());
        List<Product> market = service.readMarketStock();
        List<WarehouseProduct> warehouse = service.readWarehouse();

        boolean modified = service.replenishLowStockProducts(market, warehouse);
        assertFalse(modified);
    }

    @Test
    void shouldPartiallyReplenishWhenWarehouseLimited() throws IOException {
        Files.writeString(warehouseFile,
                "productId,name,total_stock\n" +
                "P001,Leche,30\n" +
                "P002,Pan,200\n"
        );
        service = new StoreService(stockFile.toString(), warehouseFile.toString());
        List<Product> market = service.readMarketStock();
        List<WarehouseProduct> warehouse = service.readWarehouse();

        service.replenishLowStockProducts(market, warehouse);
        assertEquals(40, market.get(0).getCurrentStock());
        assertEquals(0, warehouse.get(0).getTotalStock());
    }

    @Test
    void shouldNotModifyWhenNoProductIdMatch() throws IOException {
        Files.writeString(warehouseFile,
                "productId,name,total_stock\n" +
                "P999,Other,200\n"
        );
        service = new StoreService(stockFile.toString(), warehouseFile.toString());
        List<Product> market = service.readMarketStock();
        List<WarehouseProduct> warehouse = service.readWarehouse();

        boolean modified = service.replenishLowStockProducts(market, warehouse);
        assertFalse(modified);
    }

    @Test
    void refillProductsCompleteJourney() throws IOException {
        service.refillProducts();
        
        List<Product> market = service.readMarketStock();
        assertEquals(100, market.get(0).getCurrentStock());
    }
}
