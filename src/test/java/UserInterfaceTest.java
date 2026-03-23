import com.sts.shared.model.User;
import com.sts.client.service.UserLogin;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
public class UserInterfaceTest {

    static UserLogin userLogin = new UserLogin(
            new ArrayList<>(List.of(new User("1","paco",200.0)))
    );

    @Test
    void testValidInput(){

    }
}
