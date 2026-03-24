package com.sts.audit;

import com.sts.shared.audit.AuditLogger;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class AuditorWriterCsv  implements AuditLogger {


    private final String filePath;


    public AuditorWriterCsv(String filePath) {
        this.filePath = filePath;
    }


    @Override
    public void log(String module, EventType action, String status, String details) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filePath, true))) {
            String timestamp = LocalDateTime.now().toString();

            out.printf("%s,%s,%s,%s,%s%n", timestamp, module, action.name(), status, details);
        } catch (Exception e) {
            System.err.println("Error al escribir log: " + e.getMessage());
        }
    }
}