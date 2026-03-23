package com.sts.client.service;

import com.sts.shared.model.CSVReader;
import com.sts.shared.model.User;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserInterfaceTest {
    @Test
    void shouldExitFlow() {
        String input = String.join("\n",
                "user1", // identifier inicial
                "6"      // exit
        );

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        User user = new User("u1", "Pepe", 100.0);
        user.setName("Pepe");

        try (MockedStatic<UserLogin> mocked = Mockito.mockStatic(UserLogin.class)) {
            mocked.when(() -> UserLogin.login(Mockito.anyString()))
                    .thenReturn(user);

            UserInterface.startInterface();
        }

        String output = out.toString();

        assertTrue(output.contains("SEE YOU LATER ;)"));
    }

    @Test
    void shouldPrintIncorrectOption() {
        String input = String.join("\n",
                "user1",
                "invalid",
                "6"
        );

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        User user = new User("u1", "Pepe", 100.0);
        user.setName("Pepe");

        try (MockedStatic<UserLogin> mocked = Mockito.mockStatic(UserLogin.class)) {
            mocked.when(() -> UserLogin.login(Mockito.anyString()))
                    .thenReturn(user);

            UserInterface.startInterface();
        }

        assertTrue(out.toString().contains("INCORRECT OPTION"));
    }

    @Test
    void shouldHandleIllegalArgumentException() {
        String input = String.join("\n",
                "user1",   // login inicial OK
                "1",       // opción login
                "badUser", // este falla
                "6"
        );

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        User user = new User("u1", "Pepe", 100.0);

        try (MockedStatic<UserLogin> mocked = Mockito.mockStatic(UserLogin.class)) {

            mocked.when(() -> UserLogin.login("user1"))
                    .thenReturn(user);

            mocked.when(() -> UserLogin.login("badUser"))
                    .thenThrow(new IllegalArgumentException("Invalid user"));

            UserInterface.startInterface();
        }

        assertTrue(out.toString().contains("Invalid user"));
    }
}
