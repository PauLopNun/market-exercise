package com.sts.client.service;

import com.sts.market.service.MarketService;
import com.sts.shared.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserInterfaceTest {
    private InputStream originalIn;
    private PrintStream originalOut;
    private MarketService originalMarketService;

    @BeforeEach
    void setUp() {
        originalIn = System.in;
        originalOut = System.out;
        originalMarketService = UserInterface.marketService;
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
        UserInterface.marketService = originalMarketService;
    }

    @Test
    void shouldInstantiateUserInterface() {
        assertNotNull(new UserInterface());
    }

    @Test
    void shouldExecuteDefaultMarketLoggerLambda() throws Exception {
        UserInterface.marketService = originalMarketService;
        UserInterface.marketService.restock("NOT_EXISTS", 1);
    }

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
    void shouldExecuteBuyAndDropAndReachMenuOptionsWithoutErrors() throws Exception {
        String input = String.join("\n",
                "user1",
                "2", "P001", "2",
                "3", "P001", "1",
                "4",
                "5",
                "6"
        );

        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        MarketService marketServiceMock = Mockito.mock(MarketService.class);
        when(marketServiceMock.buy(anyString(), anyString(), anyInt())).thenReturn(true);
        UserInterface.marketService = marketServiceMock;

        User user = new User("u1", "Pepe", 100.0);
        try (MockedStatic<UserLogin> mocked = Mockito.mockStatic(UserLogin.class)) {
            mocked.when(() -> UserLogin.login(anyString())).thenReturn(user);

            UserInterface.startInterface();
        }

        verify(marketServiceMock, times(1)).buy("u1", "P001", 2);
        verify(marketServiceMock, times(1)).drop("u1", "P001", 1);
        assertTrue(out.toString().contains("SEE YOU LATER ;)"));
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

    @Test
    void shouldHandleExceptionDuringBuy() throws Exception {
        String input = String.join("\n",
                "user1",
                "2", "P001", "1",
                "6"
        );

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        MarketService marketServiceMock = Mockito.mock(MarketService.class);
        when(marketServiceMock.buy(anyString(), anyString(), anyInt()))
                .thenThrow(new RuntimeException("buy failed"));
        UserInterface.marketService = marketServiceMock;

        User user = new User("u1", "Pepe", 100.0);
        try (MockedStatic<UserLogin> mocked = Mockito.mockStatic(UserLogin.class)) {
            mocked.when(() -> UserLogin.login(anyString()))
                    .thenReturn(user);

            UserInterface.startInterface();
        }

        assertTrue(out.toString().contains("buy failed"));
    }

    @Test
    void shouldHandleLoginOptionSuccessfully() {
        String input = String.join("\n",
                "user1",
                "1",
                "user2",
                "6"
        );

        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        User user1 = new User("u1", "Pepe", 100.0);
        User user2 = new User("u2", "Ana", 120.0);

        try (MockedStatic<UserLogin> mocked = Mockito.mockStatic(UserLogin.class)) {
            mocked.when(() -> UserLogin.login("user1")).thenReturn(user1);
            mocked.when(() -> UserLogin.login("user2")).thenReturn(user2);

            UserInterface.startInterface();

            mocked.verify(() -> UserLogin.login("user2"), times(1));
        }

        assertTrue(out.toString().contains("SEE YOU LATER ;)"));
    }
}
