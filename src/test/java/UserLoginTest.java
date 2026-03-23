import com.sts.shared.model.User;
import com.sts.client.service.UserLogin;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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