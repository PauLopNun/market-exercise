package com.sts.shared;

import com.sts.shared.model.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldCreateUserWithCorrectValues() {
        User user = new User("u1", "Alice", 50.0);
        assertEquals("u1", user.getId());
        assertEquals("Alice", user.getName());
        assertEquals(50.0, user.getBudget());
    }

    @Test
    void shouldUpdateBudget() {
        User user = new User("u1", "Alice", 50.0);
        user.setBudget(30.0);
        assertEquals(30.0, user.getBudget());
    }

    @Test
    void shouldUpdateName() {
        User user = new User("u1", "Alice", 50.0);
        user.setName("Bob");
        assertEquals("Bob", user.getName());
    }

    @Test
    void shouldUpdateId() {
        User user = new User("u1", "Alice", 50.0);
        user.setId("u2");
        assertEquals("u2", user.getId());
    }
}
