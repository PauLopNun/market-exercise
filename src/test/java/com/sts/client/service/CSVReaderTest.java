package com.sts.client.service;

import com.sts.shared.model.CSVReader;
import com.sts.shared.model.User;
import com.sts.shared.model.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CSVReaderTest {

    @Test
    void shouldInstantiateCsvReader() {
        assertNotNull(new CSVReader());
    }

    @Test
    void getData() {
        List<String[]> dataExpected = new ArrayList<>();
        dataExpected.add(new String[]{"id","name","200"});
        dataExpected.add(new String[]{"id2","name2","300"});
        List<String[]> result = CSVReader.getData("./data/users.csv", ",");

        assertThat(result).containsAll(dataExpected);

    }

    private static List<Arguments> provider(){
        return List.of(Arguments.of(new User("id", "name", 200)),
                Arguments.of(new User("id2", "name2", 300)),
                Arguments.of(new User("id3", "name3", 500)));
    }
    @ParameterizedTest
    @MethodSource("provider")
    void testGetUser(User userExpected) {
        User result = CSVReader.getUser(userExpected.getName());
        assertThat(userExpected).isEqualTo(result);
    }

    @Test
    void shouldThrowWhenUserDoesNotExist() {
        assertThatThrownBy(() -> CSVReader.getUser( "does-not-exist" ))
                .isInstanceOf( UserNotFoundException.class )
                .hasMessageContaining("does-not-exist");
    }

    @Test
    void getDataFailed() {
        List<String[]> result = CSVReader.getData("", ",");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
