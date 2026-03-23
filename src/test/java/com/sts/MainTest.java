package com.sts;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MainTest {

    @Test
    void constructor_shouldCreateInstance() {
        Main main = new Main();
        assertNotNull(main);
    }

    @Test
    void main_shouldRunWithoutErrors() {
        assertDoesNotThrow(() -> Main.main(new String[0]));
    }
}

