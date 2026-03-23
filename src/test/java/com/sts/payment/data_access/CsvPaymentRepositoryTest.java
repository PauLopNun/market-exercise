package com.sts.payment.data_access;

import com.sts.payment.payment_management.PaymentDataGateway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CsvPaymentRepositoryTest {

    private PaymentDataGateway repository;

    @BeforeEach
    void createCsvFile(){
        String fileName = "users_test.csv";

        String data [] = {"1", "Ana", "25"};

        try (FileWriter writer = new FileWriter(fileName)) {

            writer.append(String.join(",", data) + "\n");
            repository = new CsvPaymentRepository(fileName);
            System.out.println("Archivo CSV creado correctamente: " + fileName);

        } catch (IOException e) {

            System.err.println("Error al escribir el archivo CSV: " + e.getMessage());

        }
    }

    @AfterEach
    void tearDownCsvFile() {

        Path path = Path.of("users_test.csv");

        try {

            Files.deleteIfExists(path);

        } catch (IOException e) {
            System.err.println("No se pudo limpiar el archivo de prueba: " + e.getMessage());
        }
    }

    @Test
    void shouldReturnUSerWhenIdExists(){

        User recoveredUser = repository.getUserById("1");

        assertEquals("Ana", recoveredUser.getName(), "El nombre no coincide");
        assertEquals(25.0, recoveredUser.getBudget(), 0.001, "El presupuesto no coincide");
    }

    @Test
    void shouldReturnNullWhenUserDoesNotExist(){
        User recoveredUser = repository.getUserById("99");

        assertNull(recoveredUser);
    }
}
