package com.sts.audit;

import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ExitTest {

    @Test
    void shouldProcessAuditLogCorrectly() throws Exception {

        String testFile = "test_audit_log.csv";

        try (PrintWriter writer = new PrintWriter(new FileWriter(testFile))) {
            writer.println("timestamp,module,action,status,details");
            writer.println("2026-03-23 10:00:00,STORE,ITEM_PURCHASED,SUCCESS,PRICE=\"19.99\" NAME=\"producto1\"");
            writer.println("2026-03-23 10:05:00,STORE,ITEM_PURCHASED,SUCCESS,PRICE=\"5.50\" NAME=\"producto2\"");
            writer.println("2026-03-23 10:10:00,PAYMENT,PAYMENT,FAILURE,USER=\"juan\"");
        }

        Map<String, Object> result = Exit.process(testFile);

        double revenue = (double) result.get("revenue");
        Map<String, Integer> products = (Map<String, Integer>) result.get("products");
        Set<String> users = (Set<String>) result.get("failedUsers");

        assertEquals(25.49, revenue, 0.01);
        assertEquals(1, products.get("producto1"));
        assertEquals(1, products.get("producto2"));
        assertTrue(users.contains("juan"));
    }
}