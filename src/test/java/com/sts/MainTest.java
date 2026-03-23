package com.sts;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MainTest {

    @Test
    void main_shouldRunWithoutErrors() {
        assertDoesNotThrow(() -> Main.main(new String[0]));
    }
}

