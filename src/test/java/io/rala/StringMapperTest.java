package io.rala;

import io.rala.testUtils.arguments.ParameterArgumentsStreamFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class StringMapperTest {
    @Test
    void mapStringToNull() {
        String s = "null";
        StringMapper stringMapper = new StringMapper(s);
        Object map = stringMapper.map(String.class);
        Assertions.assertNull(map);
    }

    @Test
    void mapStringToString() {
        String s = "string";
        StringMapper stringMapper = new StringMapper(s);
        Object map = stringMapper.map(String.class);
        Assertions.assertEquals(s, String.valueOf(map));
    }

    @ParameterizedTest
    @MethodSource("getValidMappingArguments")
    void mapValidString(Class<?> type, String s) {
        StringMapper stringMapper = new StringMapper(s);
        Object map = stringMapper.map(type);
        Assertions.assertEquals(s, String.valueOf(map));
    }

    @ParameterizedTest
    @MethodSource("getInvalidMappingArguments")
    void mapInvalidString(Class<?> type, String s) {
        StringMapper stringMapper = new StringMapper(s);
        if (type.getSimpleName().equalsIgnoreCase("Boolean") && (type.isPrimitive() || !s.equals("null"))) {
            Object map = stringMapper.map(type);
            Assertions.assertEquals("false", String.valueOf(map));
        } else if (!type.isPrimitive() && s.equals("null")) {
            Object map = stringMapper.map(type);
            Assertions.assertEquals(s, String.valueOf(map));
        } else {
            if (type.isAssignableFrom(Number.class)) {
                Assertions.assertThrows(NumberFormatException.class,
                    () -> stringMapper.map(type)
                );
            } else {
                Assertions.assertThrows(IllegalArgumentException.class,
                    () -> stringMapper.map(type)
                );
            }
        }
    }

    @Test
    void toStringOfCommandWithoutAttributes() {
        Assertions.assertEquals("text", new StringMapper("text").toString());
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
