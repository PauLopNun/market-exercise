package com.sts.market;

import com.sts.market.repository.MarketStockRepository;
import com.sts.shared.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MarketStockRepositoryTest {

    @TempDir
    Path tempDir;

    private Path csvFile;
    private MarketStockRepository repository;

    @BeforeEach
    void setUp() throws IOException {
        csvFile = tempDir.resolve("market_stock.csv");
        Files.writeString(csvFile,
                "productId,name,price,current_stock,max_capacity\n" +
                "P001,Leche,1.20,50,100\n" +
                "P002,Pan,0.80,30,80\n"
        );
        repository = new MarketStockRepository(csvFile.toString());
    }

    @Test
    void shouldReadAllProductsFromCsv() throws IOException {
        List<Product> products = repository.findAll();

        assertEquals(2, products.size());
    }

    @Test
    void shouldParseProductFieldsCorrectly() throws IOException {
        Product product = repository.findAll().get(0);

        assertEquals("P001", product.getProductId());
        assertEquals("Leche", product.getName());
        assertEquals(1.20, product.getPrice());
        assertEquals(50, product.getCurrentStock());
        assertEquals(100, product.getMaxCapacity());
    }

    @Test
    void shouldReturnEmptyListWhenCsvHasOnlyHeader() throws IOException {
        Files.writeString(csvFile, "productId,name,price,current_stock,max_capacity\n");
        List<Product> products = repository.findAll();

        assertTrue(products.isEmpty());
    }

    @Test
    void shouldSaveAllProductsToCsv() throws IOException {
        List<Product> products = repository.findAll();
        products.get(0).setCurrentStock(10);
        repository.saveAll(products);

        List<Product> reloaded = repository.findAll();
        assertEquals(10, reloaded.get(0).getCurrentStock());
    }

    @Test
    void shouldPreserveAllProductsAfterSave() throws IOException {
        List<Product> products = repository.findAll();
        repository.saveAll(products);

        List<Product> reloaded = repository.findAll();
        assertEquals(2, reloaded.size());
    }

    @Test
    void shouldOverwriteFileOnSave() throws IOException {
        List<Product> products = repository.findAll();
        products.remove(1);
        repository.saveAll(products);

        List<Product> reloaded = repository.findAll();
        assertEquals(1, reloaded.size());
    }

    @Test
    void shouldThrowIOExceptionWhenFileDoesNotExist() {
        MarketStockRepository badRepo = new MarketStockRepository("nonexistent.csv");
        assertThrows(IOException.class, badRepo::findAll);
    }

    @Test
    void shouldIgnoreBlankLinesWhenReadingStockCsv() throws IOException {
        Files.writeString(csvFile,
                "productId,name,price,current_stock,max_capacity\n" +
                "\n" +
                "P001,Leche,1.20,50,100\n"
        );

        List<Product> products = repository.findAll();
        assertEquals(1, products.size());
        assertEquals("P001", products.get(0).getProductId());
    }

    @Test
    void shouldWriteOnlyHeaderWhenSavingEmptyStock() throws IOException {
        repository.saveAll(List.of());

        String content = Files.readString(csvFile);
        assertEquals("productId,name,price,current_stock,max_capacity\n", content);
    }
}
