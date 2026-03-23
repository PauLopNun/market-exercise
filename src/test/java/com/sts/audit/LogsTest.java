package com.sts.audit;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LogsTest {

    @Test
    void returnFormatLogsFromCsv() {
        Logs logs = new Logs();

        List<String> result = logs.formatLogs();

        assertNotNull(result);
        assertEquals(3, result.size());

        assertEquals(
                "[SUCCESS] 2026-03-23 10:00:00 | PAYMENT | CREATE -> Pago realizado",
                result.get(0)
        );

        assertEquals(
                "[FAILED] 2026-03-23 10:05:00 | AUTH | LOGIN -> Contraseña incorrecta",
                result.get(1)
        );

        assertEquals(
                "[SUCCESS] 2026-03-23 10:10:00 | ORDER | UPDATE -> Pedido actualizado",
                result.get(2)
        );
    }
}