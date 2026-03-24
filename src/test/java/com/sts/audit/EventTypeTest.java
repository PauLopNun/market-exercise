package com.sts.audit;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EventTypeTest {

    @Test
    void shouldContainItemPurchased() {
        assertNotNull(EventType.ITEM_PURCHASED);
    }

    @Test
    void shouldContainItemDropped() {
        assertNotNull(EventType.ITEM_DROPPED);
    }

    @Test
    void shouldContainMarketRefilled() {
        assertNotNull(EventType.MARKET_REFILLED);
    }
}
