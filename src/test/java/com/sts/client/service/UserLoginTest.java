package com.sts.client.service;

import com.sts.shared.model.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserLoginTest {
    static UserLogin userLogin = new UserLogin();

    @Test
    void loginByName(){
        User result = userLogin.login("name");
        assertThat(result.getName()).isEqualTo("name");
    }

    @Test
    void loginById(){
        User result = userLogin.login("id");
        assertThat(result.getId()).isEqualTo("id");
    }

}