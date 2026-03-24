package com.sts.client;

import com.sts.shared.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    @TempDir
    Path tempDir;

    private Path usersFile;
    private UserRepository repository;

    @BeforeEach
    void setUp() throws IOException {
        usersFile = tempDir.resolve("users.csv");
        Files.writeString(usersFile,
                "id,name,budget\n" +
                "u1,Alice,50.0\n" +
                "u2,Bob,30.0\n"
        );
        repository = new UserRepository(usersFile.toString());
    }

    @Test
    void shouldReadAllUsers() throws IOException {
        List<User> users = repository.findAll();
        assertEquals(2, users.size());
    }

    @Test
    void shouldParseUserFieldsCorrectly() throws IOException {
        User user = repository.findAll().get(0);
        assertEquals("u1", user.getId());
        assertEquals("Alice", user.getName());
        assertEquals(50.0, user.getBudget());
    }

    @Test
    void shouldFindUserById() throws IOException {
        User user = repository.findByIdOrName("u1");
        assertNotNull(user);
        assertEquals("Alice", user.getName());
    }

    @Test
    void shouldFindUserByName() throws IOException {
        User user = repository.findByIdOrName("Alice");
        assertNotNull(user);
        assertEquals("u1", user.getId());
    }

    @Test
    void shouldFindUserByNameCaseInsensitive() throws IOException {
        User user = repository.findByIdOrName("alice");
        assertNotNull(user);
    }

    @Test
    void shouldReturnNull_whenUserNotFound() throws IOException {
        User user = repository.findByIdOrName("unknown");
        assertNull(user);
    }

    @Test
    void shouldIgnoreBlankLines() throws IOException {
        Files.writeString(usersFile, "id,name,budget\n\nu1,Alice,50.0\n");
        List<User> users = repository.findAll();
        assertEquals(1, users.size());
    }

    @Test
    void shouldThrowIOException_whenFileNotFound() {
        UserRepository bad = new UserRepository("nonexistent.csv");
        assertThrows(IOException.class, bad::findAll);
    }
}
