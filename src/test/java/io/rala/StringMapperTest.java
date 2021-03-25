package io.rala;

import io.rala.testUtils.arguments.ParameterArgumentsStreamFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class StringMapperTest {
    StringMapper stringMapper;

    @BeforeEach
    void setUp() {
        stringMapper = new StringMapper();
    }

    @Test
    void mapStringToNull() {
        String s = "null";
        Object map = stringMapper.map(s, String.class);
        Assertions.assertNull(map);
    }

    @Test
    void mapStringToString() {
        String s = "string";
        Object map = stringMapper.map(s, String.class);
        Assertions.assertEquals(s, String.valueOf(map));
    }

    @ParameterizedTest
    @MethodSource("getValidMappingArguments")
    void mapValidString(Class<?> type, String s) {
        Object map = stringMapper.map(s, type);
        Assertions.assertEquals(s, String.valueOf(map));
    }

    @ParameterizedTest
    @MethodSource("getInvalidMappingArguments")
    void mapInvalidString(Class<?> type, String s) {
        if (type.getSimpleName().equalsIgnoreCase("Boolean") && (type.isPrimitive() || !s.equals("null"))) {
            Object map = stringMapper.map(s, type);
            Assertions.assertEquals("false", String.valueOf(map));
        } else if (!type.isPrimitive() && s.equals("null")) {
            Object map = stringMapper.map(s, type);
            Assertions.assertEquals(s, String.valueOf(map));
        } else {
            if (type.isAssignableFrom(Number.class)) {
                Assertions.assertThrows(NumberFormatException.class,
                    () -> stringMapper.map(s, type)
                );
            } else {
                Assertions.assertThrows(IllegalArgumentException.class,
                    () -> stringMapper.map(s, type)
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
