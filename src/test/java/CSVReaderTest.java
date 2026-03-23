import com.sts.shared.model.CSVReader;
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
        dataExpected.add(new String[]{"hola"});
        List<String[]> result = csvReader.getData("./data/users.csv", ",");

        assertThat(dataExpected.equals(result));

    }
}
