import com.sts.shared.model.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    static User user = new User("id","paco",200.0);


    @Test
    void setBudget() {
        user.setBudget(300);
        assertThat(user.getBudget()).isEqualTo(300);
    }

    @Test
    void getBudget() {
        double userId = user.getBudget();
        assertThat(userId).isEqualTo(300);
    }

    @Test
    void setName() {
        user.setName("cambio");
        assertThat(user.getName()).isEqualTo("cambio");
    }

    @Test
    void setId() {
        user.setId("id3");
        assertThat(user.getId()).isEqualTo("id3");
    }

    @Test
    void getUserId() {
        String userId = user.getId();
        assertThat(userId).isEqualTo("id3");
    }

    @Test
    void equals() {
        user.equals(new User("id3","paco",200.0));
    }

    @Test
    void correctHashCode() {
        assertThat(user.hashCode()).isEqualTo(user.getId().hashCode());
    }
}
