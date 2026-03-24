package com.sts.audit;

import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LogsTest {

    @Test
    void returnFormatLogsFromCsv() throws Exception {

        String testFile = "test_audit_log.csv";

        try (PrintWriter writer = new PrintWriter(new FileWriter(testFile))) {
            writer.println("timestamp,module,action,status,details");
            writer.println("2026-03-23 10:00:00,STORE,ITEM_PURCHASED,SUCCESS,PRICE=\"19.99\" NAME=\"producto1\"");
            writer.println("2026-03-23 10:05:00,STORE,ITEM_PURCHASED,SUCCESS,PRICE=\"5.50\" NAME=\"producto2\"");
            writer.println("2026-03-23 10:10:00,PAYMENT,PAYMENT,FAILURE,USER=\"juan\"");
        }

        Logs logs = new Logs(testFile);

        List<String> result = logs.formatLogs();

        assertNotNull(result);
        assertEquals(3, result.size());

        assertEquals(
                "[SUCCESS] 2026-03-23 10:00:00 | ITEM_PURCHASED -> PRICE=19.99 NAME=producto1",
                result.get(0)
        );

        assertEquals(
                "[SUCCESS] 2026-03-23 10:05:00 | ITEM_PURCHASED -> PRICE=5.50 NAME=producto2",
                result.get(1)
        );

        assertEquals(
                "[FAILURE] 2026-03-23 10:10:00 | PAYMENT -> USER=juan",
                result.get(2)
        );
    }
}