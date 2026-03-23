package com.sts.audit;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventTypeTest {

    @Test
    void shouldExposeAllExpectedEventTypes() {
        assertThat(EventType.values())
                .containsExactly(EventType.ITEM_PURCHASED, EventType.ITEM_DROPPED, EventType.MARKET_REFILLED);
    }
}

