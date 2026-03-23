package com.sts.client.service;

import com.sts.shared.model.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {
    static User user = new User("id","paco",200.0);


    @Test
    void equals() {
        user.equals(new User("id3","paco",200.0));
    }

    @Test
    void correctHashCode() {
        assertThat(user.hashCode()).isEqualTo(user.getId().hashCode());
    }
}
