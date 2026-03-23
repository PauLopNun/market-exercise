package com.sts.audit;

import com.sts.audit.Logs;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LogsTest {

    @Test
    void shouldReturnFormattedLogsFromCsv() {
        Logs logs = new Logs();

        List<String> result = logs.getFormattedLogs("audit_log.csv");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.get(0).contains("PAYMENT"));
    }
}