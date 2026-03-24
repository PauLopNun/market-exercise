package com.sts.payment.data_access;

import com.sts.payment.payment_management.PaymentDataGateway;
import com.sts.shared.model.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CsvPaymentRepositoryTest {

    private PaymentDataGateway repository;

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Path.of("users_test.csv"));
        Files.deleteIfExists(Path.of("market_test.csv"));
        Files.deleteIfExists(Path.of("cart_test.csv"));
        Files.deleteIfExists(Path.of("empty.csv"));
    }

    @Test
    void shouldReturnUserWhenIdExists() throws IOException {
        String userFile = "users_test.csv";
        try (FileWriter writer = new FileWriter(userFile)) {
            writer.write("1,Ana,25.0\n");
        }

        repository = new CsvPaymentRepository(userFile, "market_test.csv", "cart_test.csv");
        User recoveredUser = repository.getUserById("1");

        assertEquals("Ana", recoveredUser.getName());
        assertEquals(25.0, recoveredUser.getBudget(), 0.001);
        assertEquals("1", recoveredUser.getId());
    }

    @Test
    void shouldReturnNullWhenUserDoesNotExist() throws IOException {
        String userFile = "users_test.csv";
        Files.createFile(Path.of(userFile));

        repository = new CsvPaymentRepository(userFile, "market_test.csv", "cart_test.csv");
        assertNull(repository.getUserById("99"));
    }

    @Test
    void shouldReturnProductWhenProductIdExists() throws IOException {
        String marketFile = "market_test.csv";
        try (FileWriter writer = new FileWriter(marketFile)) {
            writer.write("P1,Manzana,0.5,10,50\n");
        }

        repository = new CsvPaymentRepository("users_test.csv", marketFile, "cart_test.csv");
        Product recoveredProduct = repository.getProductById("P1");

        assertEquals("Manzana", recoveredProduct.getName());
        assertEquals(0.50, recoveredProduct.getPrice(), 0.001);
        assertEquals(10, recoveredProduct.getCurrentStock());
    }

    @Test
    void shouldReturnNullWhenProductDoesNotExist() throws IOException {
        String marketFile = "market_test.csv";
        Files.createFile(Path.of(marketFile));

        repository = new CsvPaymentRepository("users_test.csv", marketFile, "cart_test.csv");
        assertNull(repository.getProductById("P99"));
    }

    @Test
    void shouldReturnCartWhenUserIdExists() throws IOException {
        String cartFile = "cart_test.csv";
        try (FileWriter writer = new FileWriter(cartFile)) {
            writer.write("User1,P1,2\n");
            writer.write("User1,P2,5\n");
        }

        repository = new CsvPaymentRepository("users_test.csv", "market_test.csv", cartFile);
        List<CartItem> recoveredCartList = repository.getCartByUserId("User1");

        assertEquals(2, recoveredCartList.size());
        assertEquals("User1", recoveredCartList.get(0).getUserId());
        assertEquals(2, recoveredCartList.get(0).getQuantity());
    }

    @Test
    void shouldUpdateBalanceInFile() throws IOException {
        String userFile = "users_test.csv";
        try (FileWriter writer = new FileWriter(userFile)) {
            writer.write("1,Ana,25.0\n");
        }
        repository = new CsvPaymentRepository(userFile, "market_test.csv", "cart_test.csv");
        repository.updateBalance("1", 50.0);
        assertEquals(50.0, repository.getUserById("1").getBudget());
    }

    @Test
    void shouldNotUpdateBalanceIfUserNotFound() throws IOException {
        String userFile = "users_test.csv";
        try (FileWriter writer = new FileWriter(userFile)) {
            writer.write("1,Ana,25.0\n");
        }
        repository = new CsvPaymentRepository(userFile, "market_test.csv", "cart_test.csv");
        repository.updateBalance("99", 50.0);
        assertEquals(25.0, repository.getUserById("1").getBudget());
    }

    @Test
    void shouldClearEntireCartForUser() throws IOException {
        String cartFile = "cart_test.csv";
        try (FileWriter writer = new FileWriter(cartFile)) {
            writer.write("User1,P1,2\n");
            writer.write("User2,P1,1\n");
        }
        repository = new CsvPaymentRepository("users_test.csv", "market_test.csv", cartFile);
        repository.clearCart("User1");
        assertTrue(repository.getCartByUserId("User1").isEmpty());
        assertEquals(1, repository.getCartByUserId("User2").size());
    }

    @Test
    void shouldRemoveOnlyLastItemFromCart() throws IOException {
        String cartFile = "cart_test.csv";
        try (FileWriter writer = new FileWriter(cartFile)) {
            writer.write("User1,P1,2\n");
            writer.write("User1,P2,5\n");
        }
        repository = new CsvPaymentRepository("users_test.csv", "market_test.csv", cartFile);
        repository.removeLastItemFromCart("User1");
        List<CartItem> cart = repository.getCartByUserId("User1");
        assertEquals(1, cart.size());
        assertEquals("P1", cart.get(0).getProductId());
    }

    @Test
    void shouldDoNothingOnRemoveLastIfUserNotFound() throws IOException {
        String cartFile = "cart_test.csv";
        try (FileWriter writer = new FileWriter(cartFile)) {
            writer.write("User1,P1,2\n");
        }
        repository = new CsvPaymentRepository("users_test.csv", "market_test.csv", cartFile);
        repository.removeLastItemFromCart("User99");
        assertEquals(1, repository.getCartByUserId("User1").size());
    }

    @Test
    void shouldIncrementProductStock() throws IOException {
        String marketFile = "market_test.csv";
        try (FileWriter writer = new FileWriter(marketFile)) {
            writer.write("P1,Manzana,0.5,10,50\n");
        }
        repository = new CsvPaymentRepository("users_test.csv", marketFile, "cart_test.csv");
        repository.incrementProductStock("P1", 5);
        assertEquals(15, repository.getProductById("P1").getCurrentStock());
    }

    @Test
    void shouldNotIncrementStockIfProductNotFound() throws IOException {
        String marketFile = "market_test.csv";
        try (FileWriter writer = new FileWriter(marketFile)) {
            writer.write("P1,Manzana,0.5,10,50\n");
        }
        repository = new CsvPaymentRepository("users_test.csv", marketFile, "cart_test.csv");
        repository.incrementProductStock("P99", 5);
        assertEquals(10, repository.getProductById("P1").getCurrentStock());
    }

    @Test
    void shouldHandleIOExceptions() {
        String invalidPath = "src/main/resources";
        repository = new CsvPaymentRepository(invalidPath, invalidPath, invalidPath);

        assertNull(repository.getUserById("1"));
        assertTrue(repository.getCartByUserId("1").isEmpty());
        assertNull(repository.getProductById("1"));

        assertDoesNotThrow(() -> repository.updateBalance("1", 100));
        assertDoesNotThrow(() -> repository.clearCart("1"));
        assertDoesNotThrow(() -> repository.removeLastItemFromCart("1"));
        assertDoesNotThrow(() -> repository.incrementProductStock("1", 10));
    }

    @Test
    void pojoCoverage() {
        User user = new User("1", "A", 10);
        assertEquals("1", user.getId());
        assertEquals("A", user.getName());
        assertEquals(10, user.getBudget());

        CartItem cartItem = new CartItem("U", "P", 2);
        assertEquals("U", cartItem.getUserId());
        assertEquals("P", cartItem.getProductId());
        assertEquals(2, cartItem.getQuantity());
    }

    @Test
    void branchCoverageSpecialCases() throws IOException {

        String emptyFile = "empty.csv";
        Files.createFile(Path.of(emptyFile));
        repository = new CsvPaymentRepository(emptyFile, emptyFile, emptyFile);

        assertNull(repository.getUserById("1"));
        assertTrue(repository.getCartByUserId("1").isEmpty());

        String userFile = "users_test.csv";
        try (FileWriter writer = new FileWriter(userFile)) {
            writer.write("1,Ana,25.0\n");
        }
        repository = new CsvPaymentRepository(userFile, "market_test.csv", "cart_test.csv");

        repository.updateBalance("99", 100.0);
        repository.removeLastItemFromCart("User99");
        repository.incrementProductStock("P99", 10);

        Files.deleteIfExists(Path.of(emptyFile));
    }
}