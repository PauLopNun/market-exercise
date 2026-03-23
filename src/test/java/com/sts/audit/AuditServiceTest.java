package com.sts.audit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuditServiceTest {

    @TempDir
    Path tempDir;

    private Path logFile;
    private AuditService auditService;

    @BeforeEach
    void setUp() throws IOException {
        logFile = tempDir.resolve("audit_log.csv");
        Files.writeString(logFile, "timestamp,module,action,status,details\n");
        auditService = new AuditService(logFile.toString());
    }

    @Test
    void shouldAppendLogEntry() throws IOException {
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "User: u1 | Product: Leche | Qty: 2 | Total: 2.40€");

        List<String[]> entries = auditService.readAll();
        assertEquals(1, entries.size());
        assertEquals("MARKET", entries.get(0)[1]);
        assertEquals("ITEM_PURCHASED", entries.get(0)[2]);
        assertEquals("SUCCESS", entries.get(0)[3]);
    }

    @Test
    void shouldAppendMultipleEntries() throws IOException {
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "User: u1 | Product: Leche | Qty: 1 | Total: 1.20€");
        auditService.log("MARKET", EventType.ITEM_DROPPED, "SUCCESS", "User: u1 | ProductId: P001 | QtyReturned: 1");

        List<String[]> entries = auditService.readAll();
        assertEquals(2, entries.size());
    }

    @Test
    void shouldBeAppendOnly() throws IOException {
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "User: u1 | Product: Leche | Qty: 1 | Total: 1.20€");
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "User: u2 | Product: Pan | Qty: 1 | Total: 0.80€");

        List<String[]> entries = auditService.readAll();
        assertEquals(2, entries.size());
    }

    @Test
    void shouldCalculateTotalRevenue() throws IOException {
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "User: u1 | Product: Leche | Qty: 2 | Total: 2.40€");
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "User: u1 | Product: Pan | Qty: 1 | Total: 0.80€");

        double revenue = auditService.getTotalRevenue();
        assertEquals(3.20, revenue, 0.01);
    }

    @Test
    void shouldReturnZeroRevenueWhenNoSales() throws IOException {
        double revenue = auditService.getTotalRevenue();
        assertEquals(0.0, revenue);
    }

    @Test
    void shouldNotCountFailedPurchasesInRevenue() throws IOException {
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "FAILED", "User: u1 | ProductId: P001 | Requested: 5");
        double revenue = auditService.getTotalRevenue();
        assertEquals(0.0, revenue);
    }

    @Test
    void shouldGetTop3Products() throws IOException {
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "User: u1 | Product: Leche | Qty: 5 | Total: 6.0€");
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "User: u1 | Product: Pan | Qty: 3 | Total: 2.4€");
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "User: u1 | Product: Agua | Qty: 2 | Total: 1.0€");
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "User: u1 | Product: Arroz | Qty: 1 | Total: 0.5€");

        List<String> top3 = auditService.getTop3Products();
        assertEquals(3, top3.size());
        assertEquals("Leche", top3.get(0));
        assertEquals("Pan", top3.get(1));
        assertEquals("Agua", top3.get(2));
    }

    @Test
    void shouldReturnEmptyTop3WhenNoSales() throws IOException {
        List<String> top3 = auditService.getTop3Products();
        assertTrue(top3.isEmpty());
    }

    @Test
    void shouldGetUsersWhoExceededBudget() throws IOException {
        auditService.log("PAYMENT", EventType.ITEM_PURCHASED, "FAILED", "User: u1 | Total: 100€ | Budget: 50€");
        auditService.log("PAYMENT", EventType.ITEM_PURCHASED, "FAILED", "User: u2 | Total: 200€ | Budget: 10€");

        List<String> users = auditService.getUsersWhoExceededBudget();
        assertEquals(2, users.size());
        assertTrue(users.contains("u1"));
        assertTrue(users.contains("u2"));
    }

    @Test
    void shouldReturnEmptyListWhenNoBudgetFailures() throws IOException {
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "User: u1 | Product: Leche | Qty: 1 | Total: 1.20€");

        List<String> users = auditService.getUsersWhoExceededBudget();
        assertTrue(users.isEmpty());
    }

    @Test
    void shouldNotDuplicateUserInBudgetFailureList() throws IOException {
        auditService.log("PAYMENT", EventType.ITEM_PURCHASED, "FAILED", "User: u1 | Total: 100€ | Budget: 50€");
        auditService.log("PAYMENT", EventType.ITEM_PURCHASED, "FAILED", "User: u1 | Total: 80€ | Budget: 50€");

        List<String> users = auditService.getUsersWhoExceededBudget();
        assertEquals(1, users.size());
    }

    @Test
    void shouldIgnoreBlankLinesInLog() throws IOException {
        Files.writeString(logFile,
                "timestamp,module,action,status,details\n" +
                "\n" +
                "2026-01-01T00:00:00,MARKET,ITEM_PURCHASED,SUCCESS,User: u1 | Product: Leche | Qty: 1 | Total: 1.20€\n"
        );
        List<String[]> entries = auditService.readAll();
        assertEquals(1, entries.size());
    }

    @Test
    void shouldThrowIOExceptionWhenFileNotFound() {
        AuditService bad = new AuditService("nonexistent.csv");
        assertThrows(IOException.class, bad::readAll);
    }
    @Test
    void shouldHandleEntryWithNoTotalInRevenue() throws IOException {
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "User: u1 | Product: Leche | Qty: 1");
        assertEquals(0.0, auditService.getTotalRevenue());
    }

    @Test
    void shouldHandleEntryWithNoProductInTop3() throws IOException {
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "User: u1 | Qty: 1 | Total: 1.0€");
        assertTrue(auditService.getTop3Products().isEmpty());
    }

    @Test
    void shouldHandleEntryWithNoUserInBudgetFailures() throws IOException {
        auditService.log("PAYMENT", EventType.ITEM_PURCHASED, "FAILED", "Error: something");
        assertTrue(auditService.getUsersWhoExceededBudget().isEmpty());
    }

    @Test
    void shouldHandlePartialDetailsInGetTotalRevenue() throws IOException {
        // Test branch where part doesn't start with "Total:"
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "User: u1 | Other: value | Total: 5.50€");
        double revenue = auditService.getTotalRevenue();
        assertEquals(5.50, revenue, 0.01);
    }

    @Test
    void shouldGetTop3ProductsWithQuantityParsing() throws IOException {
        // Test branch for Qty: parsing
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "Product: Leche | Qty: 5 | Total: 6.0€");
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "Product: Pan | Qty: 3 | Total: 2.4€");
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "Product: Agua | Qty: 2 | Total: 1.0€");
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "Product: Other | Qty: 10 | Total: 5.0€");
        
        List<String> top3 = auditService.getTop3Products();
        assertEquals(3, top3.size());
        assertEquals("Other", top3.get(0));
        assertEquals("Leche", top3.get(1));
        assertEquals("Pan", top3.get(2));
    }

    @Test
    void shouldHandleGetTop3WithPartialDetails() throws IOException {
        // Missing Qty: in one entry  
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "Product: Leche | Total: 6.0€");
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "Product: Pan | Qty: 3 | Total: 2.4€");
        
        List<String> top3 = auditService.getTop3Products();
        assertTrue(top3.size() <= 2);
    }

    @Test
    void shouldHandleGetUsersWhoExceededBudgetWithPartialDetails() throws IOException {
        // Missing User: in one entry
        auditService.log("PAYMENT", EventType.ITEM_PURCHASED, "FAILED", "Total: 100€ | Budget: 50€");
        auditService.log("PAYMENT", EventType.ITEM_PURCHASED, "FAILED", "User: u1 | Total: 100€ | Budget: 50€");
        
        List<String> users = auditService.getUsersWhoExceededBudget();
        assertEquals(1, users.size());
        assertTrue(users.contains("u1"));
    }

    @Test
    void shouldHandleMultiplePartsInTotalRevenueEntry() throws IOException {
        // Entry with multiple pipes and different formatting
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "First: part | User: u1 | Total: 7.99€ | Last: part");
        double revenue = auditService.getTotalRevenue();
        assertEquals(7.99, revenue, 0.01);
    }

    @Test
    void shouldReturnZeroWhenNoTotalInFailedPurchase() throws IOException {
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "FAILED", "User: u1 | ProductId: P001");
        double revenue = auditService.getTotalRevenue();
        assertEquals(0.0, revenue);
    }

    @Test
    void shouldHandleEmptyProductNameInTop3() throws IOException {
        // Entry with empty product name
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "Product: | Qty: 5 | Total: 6.0€");
        List<String> top3 = auditService.getTop3Products();
        assertFalse(top3.contains(""));
    }

    @Test
    void shouldHandleGetTotalRevenueWithDifferentParts() throws IOException {
        // Test where Total: appears in middle with other parts
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "Qty: 2 | Total: 3.40€ | User: u1");
        double revenue = auditService.getTotalRevenue();
        assertEquals(3.40, revenue, 0.01);
    }

    @Test
    void shouldIgnorePartsNotStartingWithKeywords() throws IOException {
        // Parts that don't start with recognized keywords like "Total:" or "Product:"
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "Random: value | Product: Leche | Qty: 1 | Total: 1.20€ | Extra: data");
        double revenue = auditService.getTotalRevenue();
        assertEquals(1.20, revenue, 0.01);
        
        List<String> top3 = auditService.getTop3Products();
        assertEquals(1, top3.size());
        assertEquals("Leche", top3.get(0));
    }

    @Test
    void shouldGetTop3ProductsWithZeroQuantity() throws IOException {
        // Entry with Qty: 0 - still gets added to map (0 count)
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "Product: Leche | Qty: 0 | Total: 0.0€");
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "Product: Pan | Qty: 5 | Total: 4.0€");
        
        List<String> top3 = auditService.getTop3Products();
        assertEquals(2, top3.size());
        assertEquals("Pan", top3.get(0));
        assertEquals("Leche", top3.get(1));
    }

    @Test
    void shouldHandleGetUsersWhoExceededBudgetWithVariousFormats() throws IOException {
        // Entry with User: but no total or budget
        auditService.log("PAYMENT", EventType.ITEM_PURCHASED, "FAILED", "User: u1 | Error: insufficient");
        auditService.log("PAYMENT", EventType.ITEM_PURCHASED, "FAILED", "User: u2 | Total: 100€");
        
        List<String> users = auditService.getUsersWhoExceededBudget();
        assertEquals(2, users.size());
    }

    @Test
    void shouldHandleTotalWithCurrencyAndSpaces() throws IOException {
        // Test parsing of currency symbol and spaces
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "User: u1 | Product: Leche | Qty: 1 | Total: 5.99€");
        double revenue = auditService.getTotalRevenue();
        assertEquals(5.99, revenue, 0.01);
    }

    @Test
    void shouldHandleProductNameWithSpaces() throws IOException {
        // Product with spaces in name
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "Product: Leche Entera | Qty: 2 | Total: 2.40€");
        List<String> top3 = auditService.getTop3Products();
        assertTrue(top3.contains("Leche Entera"));
    }

    @Test
    void shouldHandleMultipleEntriesWithSameProduct() throws IOException {
        // Multiple entries of same product to accumulate quantity
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "Product: Leche | Qty: 3 | Total: 3.60€");
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "Product: Leche | Qty: 2 | Total: 2.40€");
        
        List<String> top3 = auditService.getTop3Products();
        assertEquals(1, top3.size());
        assertEquals("Leche", top3.get(0)); // Should have qty = 5 total
    }

    @Test
    void shouldHandleEntryWithMultipleUserFields() throws IOException {
        // Entry with User: appearing multiple times - should take first
        auditService.log("PAYMENT", EventType.ITEM_PURCHASED, "FAILED", "User: u1 | Other: User: u2 | Total: 100€");
        List<String> users = auditService.getUsersWhoExceededBudget();
        assertTrue(users.contains("u1"));
    }

    @Test
    void shouldHandleGetTotalRevenueWithMultipleFailedAndSuccess() throws IOException {
        // Mix of SUCCESS and FAILED - only count SUCCESS
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "User: u1 | Product: Leche | Qty: 1 | Total: 1.20€");
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "FAILED", "User: u2 | Product: Pan | Qty: 1 | Total: 5.0€");
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "User: u3 | Product: Agua | Qty: 2 | Total: 2.00€");
        
        double revenue = auditService.getTotalRevenue();
        assertEquals(3.20, revenue, 0.01); // Only SUCCESS entries
    }

    @Test
    void shouldGetTop3WithUnorderableQuantities() throws IOException {
        // Negative or very large quantities
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "Product: Leche | Qty: 100 | Total: 120.0€");
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "Product: Pan | Qty: 50 | Total: 40.0€");
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "Product: Agua | Qty: 10 | Total: 5.0€");
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "Product: Arroz | Qty: 1 | Total: 1.0€");
        
        List<String> top3 = auditService.getTop3Products();
        assertEquals(3, top3.size());
        assertEquals("Leche", top3.get(0));
        assertEquals("Pan", top3.get(1));
        assertEquals("Agua", top3.get(2));
    }

    @Test
    void shouldIgnoreItemDroppedEventsInRevenue() throws IOException {
        // ITEM_DROPPED events should be ignored (not ITEM_PURCHASED)
        auditService.log("MARKET", EventType.ITEM_DROPPED, "SUCCESS", "Product: Leche | Qty: 1 | Total: 1.20€");
        double revenue = auditService.getTotalRevenue();
        assertEquals(0.0, revenue);
    }

    @Test
    void shouldIgnoreFailedStatusInGetTop3Products() throws IOException {
        // FAILED status should not be counted in getTop3Products (only SUCCESS)
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "FAILED", "Product: Leche | Qty: 100 | Total: 120.0€");
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "Product: Pan | Qty: 1 | Total: 0.80€");
        
        List<String> top3 = auditService.getTop3Products();
        assertEquals(1, top3.size());
        assertEquals("Pan", top3.get(0));
    }

    @Test
    void shouldIgnoreFailedStatusInGetUsersWhoExceededBudget() throws IOException {
        // Only FAILED status should be counted in getUsersWhoExceededBudget
        auditService.log("PAYMENT", EventType.ITEM_PURCHASED, "SUCCESS", "User: u1 | Total: 100€ | Budget: 50€");
        auditService.log("PAYMENT", EventType.ITEM_PURCHASED, "FAILED", "User: u2 | Total: 100€ | Budget: 50€");
        
        List<String> users = auditService.getUsersWhoExceededBudget();
        assertEquals(1, users.size());
        assertTrue(users.contains("u2"));
    }

    @Test
    void shouldIgnoreNonItemPurchasedEvents() throws IOException {
        // Only ITEM_PURCHASED events should count in getTotalRevenue
        auditService.log("MARKET", EventType.ITEM_DROPPED, "SUCCESS", "Product: Leche | Qty: 1 | Total: 1.20€");
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "Product: Pan | Qty: 1 | Total: 0.80€");
        
        double revenue = auditService.getTotalRevenue();
        assertEquals(0.80, revenue, 0.01);
    }

    @Test
    void shouldHandleNonITEM_PURCHASEDInGetTop3() throws IOException {
        // Only ITEM_PURCHASED events should be counted
        auditService.log("MARKET", EventType.ITEM_DROPPED, "SUCCESS", "Product: Leche | Qty: 100 | Total: 120.0€");
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "Product: Pan | Qty: 5 | Total: 4.0€");
        
        List<String> top3 = auditService.getTop3Products();
        assertEquals(1, top3.size());
        assertEquals("Pan", top3.get(0));
    }

    @Test
    void shouldHandleNonFailedStatusInGetUsersWhoExceededBudget() throws IOException {
        // Only FAILED status and ITEM_PURCHASED events should count
        auditService.log("PAYMENT", EventType.ITEM_PURCHASED, "SUCCESS", "User: u1 | Total: 100€");
        auditService.log("PAYMENT", EventType.ITEM_DROPPED, "FAILED", "User: u2 | Total: 100€");
        
        List<String> users = auditService.getUsersWhoExceededBudget();
        // Both conditions must be met: ITEM_PURCHASED AND FAILED
        assertTrue(users.isEmpty());
    }

    @Test
    void shouldHandleTwoConditionsInGetTotalRevenue() throws IOException {
        // Must have both ITEM_PURCHASED AND SUCCESS
        auditService.log("MARKET", EventType.ITEM_DROPPED, "SUCCESS", "User: u1 | Total: 1.20€");
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "FAILED", "User: u2 | Total: 0.80€");
        auditService.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS", "User: u3 | Total: 5.00€");
        
        double revenue = auditService.getTotalRevenue();
        assertEquals(5.00, revenue, 0.01);
    }
}
