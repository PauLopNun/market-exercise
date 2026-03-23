package com.sts.audit;

import com.sts.shared.audit.AuditLogger;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AuditService implements AuditLogger {

    private final String filePath;

    public AuditService(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void log(String module, EventType action, String status, String details) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String line = String.join(",", timestamp, module, action.name(), status, details);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(line + "\n");
        }
    }

    public List<String[]> readAll() throws IOException {
        List<String[]> entries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                entries.add(line.split(",", 5));
            }
        }
        return entries;
    }

    public double getTotalRevenue() throws IOException {
        double total = 0;
        for (String[] entry : readAll()) {
            if (entry[2].equals("ITEM_PURCHASED") && entry[3].equals("SUCCESS")) {
                String details = entry[4];
                for (String part : details.split("\\|")) {
                    part = part.trim();
                    if (part.startsWith("Total:")) {
                        String value = part.replace("Total:", "").replace("€", "").trim();
                        total += Double.parseDouble(value);
                    }
                }
            }
        }
        return total;
    }

    public List<String> getTop3Products() throws IOException {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String[] entry : readAll()) {
            if (entry[2].equals("ITEM_PURCHASED") && entry[3].equals("SUCCESS")) {
                String details = entry[4];
                String productName = "";
                int qty = 0;
                for (String part : details.split("\\|")) {
                    part = part.trim();
                    if (part.startsWith("Product:")) productName = part.replace("Product:", "").trim();
                    if (part.startsWith("Qty:")) qty = Integer.parseInt(part.replace("Qty:", "").trim());
                }
                if (!productName.isEmpty()) {
                    counts.merge(productName, qty, Integer::sum);
                }
            }
        }
        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();
    }

    public List<String> getUsersWhoExceededBudget() throws IOException {
        Set<String> users = new LinkedHashSet<>();
        for (String[] entry : readAll()) {
            if (entry[2].equals("ITEM_PURCHASED") && entry[3].equals("FAILED")) {
                String details = entry[4];
                for (String part : details.split("\\|")) {
                    part = part.trim();
                    if (part.startsWith("User:")) {
                        users.add(part.replace("User:", "").trim());
                    }
                }
            }
        }
        return new ArrayList<>(users);
    }
}
