package io.rala;

import io.rala.testUtils.arguments.ParameterArgumentsStreamFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class StringMapperTest {
    private StringMapper stringMapper;

    @BeforeEach
    void setUp() {
        stringMapper = new StringMapper();
    }

    @Test
    void mapStringToNull() {
        String s = "null";
        Object map = StringMapper.getInstance().map(s, String.class);
        assertNull(map);
    }

    @Test
    void mapStringToString() {
        String s = "string";
        Object map = StringMapper.getInstance().map(s, String.class);
        assertEquals(s, String.valueOf(map));
    }

    @Test
    void mapStringToLocalDateWithoutMapper() {
        String s = "2018-11-25";

        assertThrows(IllegalArgumentException.class,
            () -> StringMapper.getInstance().map(s, LocalDate.class)
        );
    }

    @Test
    void mapStringToLocalDateWithMapper() {
        String s = "2018-11-25";

        stringMapper.addCustomMapper(LocalDate.class, LocalDate::parse);

        Object map = stringMapper.map(s, LocalDate.class);
        assertEquals(LocalDate.of(2018, 11, 25), map);

        stringMapper.removeCustomMapper(LocalDate.class);
        assertThrows(IllegalArgumentException.class,
            () -> stringMapper.map(s, LocalDate.class)
        );
    }

    @ParameterizedTest
    @MethodSource("getValidMappingArguments")
    void mapValidString(Class<?> type, String s) {
        Object map = StringMapper.getInstance().map(s, type);
        assertEquals(s, String.valueOf(map));
    }

    @ParameterizedTest
    @MethodSource("getInvalidMappingArguments")
    void mapInvalidString(Class<?> type, String s) {
        if (type.getSimpleName().equalsIgnoreCase("Boolean") && (type.isPrimitive() || !s.equals("null"))) {
            Object map = StringMapper.getInstance().map(s, type);
            assertEquals("false", String.valueOf(map));
        } else if (!type.isPrimitive() && s.equals("null")) {
            Object map = StringMapper.getInstance().map(s, type);
            assertEquals(s, String.valueOf(map));
        } else {
            if (type.isAssignableFrom(Number.class)) {
                assertThrows(NumberFormatException.class,
                    () -> StringMapper.getInstance().map(s, type)
                );
            } else {
                assertThrows(IllegalArgumentException.class,
                    () -> StringMapper.getInstance().map(s, type)
                );
            }
        }
    }

    // region arguments stream

    private static Stream<Arguments> getValidMappingArguments() {
        return ParameterArgumentsStreamFactory.validMapping();
    }

    private static Stream<Arguments> getInvalidMappingArguments() {
        return ParameterArgumentsStreamFactory.invalidMapping();
    }

    // endregion
}
