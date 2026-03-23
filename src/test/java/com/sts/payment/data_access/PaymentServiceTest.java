package com.sts.payment.data_access;

import com.sts.payment.integration.PaymentAuditor;
import com.sts.payment.integration.PaymentCoordinator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class PaymentServiceTest {
    @Test
    void exceptionWhenAmountIsNegative() {
        PaymentCoordinator coordinator = new PaymentCoordinator();

        assertThrows(IllegalArgumentException.class, () -> {
            coordinator.processPayment("user1", -10.0);
        });
    }
}
