package com.sts.client.service;

import com.sts.shared.model.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {
    static User user = new User("id","paco",200.0);


    @Test
    void equals_shouldReturnTrue_whenSameReference() {
        assertThat(user.equals(user)).isTrue();
    }

    @Test
    void equals_shouldReturnFalse_whenComparedWithNull() {
        assertThat(user.equals(null)).isFalse();
    }

    @Test
    void equals_shouldReturnFalse_whenComparedWithDifferentClass() {
        assertThat(user.equals("id")).isFalse();
    }

    @Test
    void equals_shouldReturnTrue_whenIdsMatch() {
        assertThat(user.equals(new User("id","otro",999.0))).isTrue();
    }

    @Test
    void equals_shouldReturnFalse_whenIdsDoNotMatch() {
        assertThat(user.equals(new User("id3","paco",200.0))).isFalse();
    }

    @Test
    void correctHashCode() {
        assertThat(user.hashCode()).isEqualTo(user.getId().hashCode());
    }
}
