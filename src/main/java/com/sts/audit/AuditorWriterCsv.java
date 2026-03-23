package com.sts.audit;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class AuditorWriterCsv {
    private static final String FILE_PATH = "data/audit_log.csv";

    public static void log(EventType type, String message) {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            String timestamp = LocalDateTime.now().toString();
            out.printf("%s, %s, %s%n", timestamp, type, message);
        } catch (Exception e) {
            System.err.println("Error al escribir log: " + e.getMessage());
        }
    }

}

