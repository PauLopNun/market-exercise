package com.sts;

import com.sts.client.service.UserInterface;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

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
        try (MockedStatic<UserInterface> mockedUi = Mockito.mockStatic(UserInterface.class)) {
            assertDoesNotThrow(() -> Main.main(new String[0]));
            mockedUi.verify(UserInterface::startInterface);
        }
    }
}

