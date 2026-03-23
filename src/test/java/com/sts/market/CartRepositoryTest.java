package com.sts.market;

import com.sts.market.repository.CartRepository;
import com.sts.shared.model.CartEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CartRepositoryTest {

    @TempDir
    Path tempDir;

    private Path csvFile;
    private CartRepository repository;

    @BeforeEach
    void setUp() throws IOException {
        csvFile = tempDir.resolve("cart.csv");
        Files.writeString(csvFile, "userId,productId,quantity\n");
        repository = new CartRepository(csvFile.toString());
    }

    @Test
    void shouldReturnEmptyCartOnStart() throws IOException {
        List<CartEntry> entries = repository.findAll();
        assertTrue(entries.isEmpty());
    }

    @Test
    void shouldAddNewEntryToEmptyCart() throws IOException {
        repository.addOrUpdate("u1", "P001", 3);

        List<CartEntry> entries = repository.findAll();
        assertEquals(1, entries.size());
        assertEquals(3, entries.get(0).getQuantity());
    }

    @Test
    void shouldAccumulateQuantityWhenSameProductAddedTwice() throws IOException {
        repository.addOrUpdate("u1", "P001", 2);
        repository.addOrUpdate("u1", "P001", 3);

        List<CartEntry> entries = repository.findAll();
        assertEquals(1, entries.size());
        assertEquals(5, entries.get(0).getQuantity());
    }

    @Test
    void shouldAddSeparateEntriesForDifferentProducts() throws IOException {
        repository.addOrUpdate("u1", "P001", 2);
        repository.addOrUpdate("u1", "P002", 1);

        List<CartEntry> entries = repository.findAll();
        assertEquals(2, entries.size());
    }

    @Test
    void shouldAddSeparateEntriesForDifferentUsers() throws IOException {
        repository.addOrUpdate("u1", "P001", 2);
        repository.addOrUpdate("u2", "P001", 1);

        List<CartEntry> entries = repository.findAll();
        assertEquals(2, entries.size());
    }

    @Test
    void shouldRemoveEntryWhenQuantityDropsToZero() throws IOException {
        repository.addOrUpdate("u1", "P001", 3);
        repository.remove("u1", "P001", 3);

        List<CartEntry> entries = repository.findAll();
        assertTrue(entries.isEmpty());
    }

    @Test
    void shouldRemoveEntryWhenRemovingMoreThanAvailable() throws IOException {
        repository.addOrUpdate("u1", "P001", 2);
        repository.remove("u1", "P001", 5);

        List<CartEntry> entries = repository.findAll();
        assertTrue(entries.isEmpty());
    }

    @Test
    void shouldDecrementQuantityWhenPartialRemove() throws IOException {
        repository.addOrUpdate("u1", "P001", 5);
        repository.remove("u1", "P001", 2);

        List<CartEntry> entries = repository.findAll();
        assertEquals(1, entries.size());
        assertEquals(3, entries.get(0).getQuantity());
    }

    @Test
    void shouldNotAffectOtherEntriesOnRemove() throws IOException {
        repository.addOrUpdate("u1", "P001", 3);
        repository.addOrUpdate("u1", "P002", 2);
        repository.remove("u1", "P001", 3);

        List<CartEntry> entries = repository.findAll();
        assertEquals(1, entries.size());
        assertEquals("P002", entries.get(0).getProductId());
    }

    @Test
    void shouldDoNothingWhenRemovingNonExistentEntry() throws IOException {
        repository.addOrUpdate("u1", "P001", 3);
        repository.remove("u1", "P999", 1);

        List<CartEntry> entries = repository.findAll();
        assertEquals(1, entries.size());
    }

    @Test
    void shouldThrowIOExceptionWhenFileDoesNotExist() {
        CartRepository badRepo = new CartRepository("nonexistent.csv");
        assertThrows(IOException.class, badRepo::findAll);
    }

    @Test
    void shouldIgnoreBlankLinesWhenReadingCartCsv() throws IOException {
        Files.writeString(csvFile,
                "userId,productId,quantity\n" +
                "\n" +
                "u1,P001,2\n"
        );

        List<CartEntry> entries = repository.findAll();
        assertEquals(1, entries.size());
        assertEquals("u1", entries.get(0).getUserId());
        assertEquals("P001", entries.get(0).getProductId());
        assertEquals(2, entries.get(0).getQuantity());
    }

    @Test
    void shouldWriteOnlyHeaderWhenSavingEmptyCart() throws IOException {
        repository.saveAll(List.of());

        String content = Files.readString(csvFile);
        assertEquals("userId,productId,quantity\n", content);
    }
}
