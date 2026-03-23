package com.sts.market;

import com.sts.market.repository.CartRepository;
import com.sts.market.repository.MarketStockRepository;
import com.sts.market.service.MarketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MarketServiceTest {

    @TempDir
    Path tempDir;

    private MarketService service;
    private MarketStockRepository stockRepo;
    private CartRepository cartRepo;

    @BeforeEach
    void setUp() throws IOException {
        Path stockFile = tempDir.resolve("market_stock.csv");
        Path cartFile  = tempDir.resolve("cart.csv");

        Files.writeString(stockFile,
                "productId,name,price,current_stock,max_capacity\n" +
                "P001,Leche,1.20,10,100\n" +
                "P002,Pan,0.80,0,80\n"
        );
        Files.writeString(cartFile, "userId,productId,quantity\n");

        stockRepo = new MarketStockRepository(stockFile.toString());
        cartRepo  = new CartRepository(cartFile.toString());
        service   = new MarketService(stockRepo, cartRepo);
    }

    // ── BUY ──────────────────────────────────────────────────────────────────

    @Test
    void buy_shouldReturnTrue_whenStockIsSufficient() throws IOException {
        boolean result = service.buy("u1", "P001", 3);
        assertTrue(result);
    }

    @Test
    void buy_shouldDecrementStock_whenPurchaseSucceeds() throws IOException {
        service.buy("u1", "P001", 3);

        int stock = stockRepo.findAll().stream()
                .filter(p -> p.getProductId().equals("P001"))
                .findFirst().get().getCurrentStock();

        assertEquals(7, stock);
    }

    @Test
    void buy_shouldAddEntryToCart_whenPurchaseSucceeds() throws IOException {
        service.buy("u1", "P001", 3);

        long count = cartRepo.findAll().stream()
                .filter(e -> e.getUserId().equals("u1") && e.getProductId().equals("P001"))
                .count();

        assertEquals(1, count);
    }

    @Test
    void buy_shouldReturnFalse_whenStockIsZero() throws IOException {
        boolean result = service.buy("u1", "P002", 1);
        assertFalse(result);
    }

    @Test
    void buy_shouldReturnFalse_whenRequestedQtyExceedsStock() throws IOException {
        boolean result = service.buy("u1", "P001", 99);
        assertFalse(result);
    }

    @Test
    void buy_shouldNotModifyStock_whenPurchaseFails() throws IOException {
        service.buy("u1", "P001", 99);

        int stock = stockRepo.findAll().stream()
                .filter(p -> p.getProductId().equals("P001"))
                .findFirst().get().getCurrentStock();

        assertEquals(10, stock);
    }

    @Test
    void buy_shouldNotAddToCart_whenPurchaseFails() throws IOException {
        service.buy("u1", "P001", 99);
        assertTrue(cartRepo.findAll().isEmpty());
    }

    @Test
    void buy_shouldReturnFalse_whenProductDoesNotExist() throws IOException {
        boolean result = service.buy("u1", "P999", 1);
        assertFalse(result);
    }

    @Test
    void buy_shouldSucceed_whenRequestedQtyEqualsStock() throws IOException {
        boolean result = service.buy("u1", "P001", 10);
        assertTrue(result);
    }

    @Test
    void buy_shouldReturnFalse_whenStockListIsEmpty() throws IOException {
        stockRepo.saveAll(java.util.List.of());

        boolean result = service.buy("u1", "P001", 1);
        assertFalse(result);
    }

    // ── DROP ─────────────────────────────────────────────────────────────────

    @Test
    void drop_shouldRestoreStock_whenItemIsInCart() throws IOException {
        service.buy("u1", "P001", 3);
        service.drop("u1", "P001", 2);

        int stock = stockRepo.findAll().stream()
                .filter(p -> p.getProductId().equals("P001"))
                .findFirst().get().getCurrentStock();

        assertEquals(9, stock);
    }

    @Test
    void drop_shouldRemoveEntryFromCart_whenFullQtyDropped() throws IOException {
        service.buy("u1", "P001", 3);
        service.drop("u1", "P001", 3);

        assertTrue(cartRepo.findAll().isEmpty());
    }

    @Test
    void drop_shouldReduceCartQuantity_whenPartialDrop() throws IOException {
        service.buy("u1", "P001", 5);
        service.drop("u1", "P001", 2);

        int qty = cartRepo.findAll().stream()
                .filter(e -> e.getProductId().equals("P001"))
                .findFirst().get().getQuantity();

        assertEquals(3, qty);
    }

    @Test
    void drop_shouldOnlyRestoreActualQty_whenDropExceedsCartQty() throws IOException {
        service.buy("u1", "P001", 3);
        service.drop("u1", "P001", 99);

        int stock = stockRepo.findAll().stream()
                .filter(p -> p.getProductId().equals("P001"))
                .findFirst().get().getCurrentStock();

        assertEquals(10, stock);
    }

    @Test
    void drop_shouldDoNothing_whenProductNotInCart() throws IOException {
        service.drop("u1", "P999", 1);

        int stock = stockRepo.findAll().stream()
                .filter(p -> p.getProductId().equals("P001"))
                .findFirst().get().getCurrentStock();

        assertEquals(10, stock);
    }

    @Test
    void drop_shouldDoNothing_whenRequestedQtyIsZero() throws IOException {
        service.buy("u1", "P001", 2);
        service.drop("u1", "P001", 0);

        int stock = stockRepo.findAll().stream()
                .filter(p -> p.getProductId().equals("P001"))
                .findFirst().get().getCurrentStock();

        int qty = cartRepo.findAll().stream()
                .filter(e -> e.getUserId().equals("u1") && e.getProductId().equals("P001"))
                .findFirst().get().getQuantity();

        assertEquals(8, stock);
        assertEquals(2, qty);
    }

    @Test
    void drop_shouldRemoveFromCartEvenWhenProductNoLongerExistsInStock() throws IOException {
        service.buy("u1", "P001", 2);

        stockRepo.saveAll(java.util.List.of());
        service.drop("u1", "P001", 2);

        assertTrue(cartRepo.findAll().isEmpty());
        assertTrue(stockRepo.findAll().isEmpty());
    }

    // ── RESTOCK ──────────────────────────────────────────────────────────────

    @Test
    void restock_shouldIncreaseStock_whenProductExists() throws IOException {
        service.restock("P001", 5);

        int stock = stockRepo.findAll().stream()
                .filter(p -> p.getProductId().equals("P001"))
                .findFirst().get().getCurrentStock();

        assertEquals(15, stock);
    }

    @Test
    void restock_shouldDoNothing_whenProductDoesNotExist() throws IOException {
        service.restock("P999", 5);

        int stock = stockRepo.findAll().stream()
                .filter(p -> p.getProductId().equals("P001"))
                .findFirst().get().getCurrentStock();

        assertEquals(10, stock);
    }

    @Test
    void restock_shouldWorkOnProductWithZeroStock() throws IOException {
        service.restock("P002", 10);

        int stock = stockRepo.findAll().stream()
                .filter(p -> p.getProductId().equals("P002"))
                .findFirst().get().getCurrentStock();

        assertEquals(10, stock);
    }

    @Test
    void restock_shouldDoNothing_whenStockListIsEmpty() throws IOException {
        stockRepo.saveAll(java.util.List.of());
        service.restock("P001", 10);

        assertTrue(stockRepo.findAll().isEmpty());
    }
}
