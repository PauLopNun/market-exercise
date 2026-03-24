package com.sts.audit;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MethodTest {

    private Path tempFile;
    private AuditorWriterCsv auditor;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = Files.createTempFile("audit-test-", ".csv");
        auditor = new AuditorWriterCsv(tempFile.toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    void testLogWritesCsvLine() throws IOException {
        auditor.log("AuthModule", EventType.LOGIN, "SUCCESS", "Usuario admin inició sesión");

        List<String> lines = Files.readAllLines(tempFile);
        assertEquals(1, lines.size(), "Debe haber una línea en el CSV");

        String line = lines.get(0);

        assertTrue(line.contains("AuthModule"), "Debe contener el módulo");
        assertTrue(line.contains("LOGIN"), "Debe contener el action");
        assertTrue(line.contains("SUCCESS"), "Debe contener el status");
        assertTrue(line.contains("Usuario admin inició sesión"), "Debe contener los detalles");

        assertTrue(line.matches("^\\d{4}-\\d{2}-\\d{2}.*"), "Debe empezar con la fecha");
    }
}