package com.sts.audit;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Logs {

    public List<String> formatLogs(String filePath) {
        List<String> Logs = new ArrayList<>();

        try {
            InputStream is = getClass()
                    .getClassLoader()
                    .getResourceAsStream(filePath);

            if (is == null) {
                throw new RuntimeException("Archivo no encontrado: " + filePath);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {

                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] parts = line.split(",");

                if (parts.length < 5) {
                    continue;
                }

                String timestamp = parts[0].trim();
                String module = parts[1].trim();
                String action = parts[2].trim();
                String status = parts[3].trim();
                String details = parts[4].trim();

                String formatted = String.format(
                        "[%s] %s | %s | %s -> %s",
                        status,
                        timestamp,
                        module,
                        action,
                        details
                );

                formattedLogs.add(formatted);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error procesando logs", e);
        }

        return formattedLogs;
    }
}