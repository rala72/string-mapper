package io.rala;

import io.rala.testUtils.arguments.ParameterArgumentsStreamFactory;
import io.rala.testUtils.model.ChildTestClass;
import io.rala.testUtils.model.InterfaceTestClass;
import io.rala.testUtils.model.ParentTestClass;
import io.rala.testUtils.model.TestInterface;
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
        assertEquals(LocalDate.class, map.getClass());
        assertEquals(LocalDate.of(2018, 11, 25), map);

        stringMapper.removeCustomMapper(LocalDate.class);
        assertThrows(IllegalArgumentException.class,
            () -> stringMapper.map(s, LocalDate.class)
        );
    }

    @Test
    void mapStringToParentTestClassWithChildMapper() {
        String s = "test";

        stringMapper.addCustomMapper(ChildTestClass.class, ChildTestClass::new);

        assertThrows(IllegalArgumentException.class,
            () -> stringMapper.map(s, ParentTestClass.class)
        );

        Object map = stringMapper.map(s, ChildTestClass.class);
        assertEquals(ChildTestClass.class, map.getClass());
        assertEquals(new ChildTestClass(s), map);
    }

    @Test
    void mapStringToChildTestClassWithParentMapper() {
        String s = "test";

        stringMapper.addCustomMapper(ParentTestClass.class, ParentTestClass::new);

        Object map;
        map = stringMapper.map(s, ParentTestClass.class);
        assertEquals(ParentTestClass.class, map.getClass());
        assertEquals(new ParentTestClass(s), map);

        map = stringMapper.map(s, ChildTestClass.class);
        assertNotEquals(ChildTestClass.class, map.getClass());
        assertEquals(new ChildTestClass(s), map);
    }

    @Test
    void mapStringToClassWithInterfaceMapper() {
        String s = "test";

        stringMapper.addCustomMapper(InterfaceTestClass.class, InterfaceTestClass::new);

        assertThrows(IllegalArgumentException.class,
            () -> stringMapper.map(s, TestInterface.class)
        );

        Object map = stringMapper.map(s, InterfaceTestClass.class);
        assertEquals(InterfaceTestClass.class, map.getClass());
        assertEquals(new InterfaceTestClass(s), map);
    }

    @Test
    void mapStringToInterfaceWithClassMapper() {
        String s = "test";

        stringMapper.addCustomMapper(TestInterface.class, InterfaceTestClass::new);

        Object map;
        map = stringMapper.map(s, InterfaceTestClass.class);
        assertEquals(InterfaceTestClass.class, map.getClass());
        assertEquals(new InterfaceTestClass(s), map);

        map = stringMapper.map(s, TestInterface.class);
        assertNotEquals(TestInterface.class, map.getClass());
        assertEquals(new InterfaceTestClass(s), map);
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
