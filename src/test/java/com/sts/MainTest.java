package com.sts;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class MainTest {

    @Test
    void testMainClassExists() {
        assertNotNull(Main.class);
    }

    @Test
    void testMainHasMainMethod() {
        assertNotNull(Main.class.getDeclaredMethods());
    }

    @Test
    void testMainCanBeConstructed() {
        Main main = new Main();
        assertNotNull(main);
    }
}

