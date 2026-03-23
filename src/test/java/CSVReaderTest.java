import com.sts.shared.model.CSVReader;
import com.sts.shared.model.User;
import org.junit.jupiter.api.Test;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class CSVReaderTest {
    CSVReader csvReader = new CSVReader();

    @Test
    void getData() {
        List<String[]> dataExpected = new ArrayList<>();
        dataExpected.add(
                new String[]{"id", "name", "200"}
        );
        dataExpected.add(
                new String[]{"id2", "name2", "300"}
        );
        List<String[]> result = csvReader.getData("./data/users.csv", ",");

        assertThat(result).containsAll(dataExpected);

    }

    @Test
    void testUserNotFoundException() {
        User userExpected = new User("id2", "name", 200);
        User result = csvReader.getUser("id2");
        assertThat(userExpected).isEqualTo(result);

    }
}
