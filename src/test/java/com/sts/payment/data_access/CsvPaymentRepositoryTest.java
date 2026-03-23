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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CsvPaymentRepositoryTest {

    private PaymentDataGateway repository;

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Path.of("users_test.csv"));
        Files.deleteIfExists(Path.of("market_test.csv"));
        Files.deleteIfExists(Path.of("cart_test.csv"));
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
    }

    @Test
    void shouldReturnNullWhenUserDoesNotExist() throws IOException {
        String userFile = "users_test.csv";
        Files.createFile(Path.of(userFile));

        repository = new CsvPaymentRepository(userFile, "market_test.csv", "cart_test.csv");
        User recoveredUser = repository.getUserById("99");

        assertNull(recoveredUser);
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
        Product recoveredProduct = repository.getProductById("P99");

        assertNull(recoveredProduct);
    }

    @Test
    void shouldReturnCartWhenUserIdExists() throws IOException {
        String cartFile = "cart_test.csv";
        try (FileWriter writer = new FileWriter(cartFile)) {
            writer.write("User1,P1,2\n");
            writer.write("User1,P2,5\n");
            writer.write("User2,P1,1\n");
        }

        repository = new CsvPaymentRepository("users_test.csv", "market_test.csv", cartFile);
        List<CartItem> recoveredCartList = repository.getCartByUserId("User1");

        assertEquals(2, recoveredCartList.size());
        assertEquals("P1", recoveredCartList.get(0).getProductId());
        assertEquals("P2", recoveredCartList.get(1).getProductId());
    }
}