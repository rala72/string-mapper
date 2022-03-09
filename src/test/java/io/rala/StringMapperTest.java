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
import java.time.Month;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class StringMapperTest {
    private StringMapper stringMapper;

    @BeforeEach
    void setUp() {
        stringMapper = new StringMapper();
    }

    // region mapString basic and custom

    @Test
    void mapStringToNull() {
        String s = "null";
        Object map = StringMapper.getInstance().map(s, String.class);
        assertThat(map).isNull();
    }

    @Test
    void mapStringToString() {
        String s = "string";
        Object map = StringMapper.getInstance().map(s, String.class);
        assertThat(String.valueOf(map)).isEqualTo(s);
    }

    @Test
    void mapStringToEnum() {
        String s = "APRIL";

        stringMapper.addEnumMapper();

        Object map = stringMapper.map(s, Month.class);
        assertThat(map).isNotNull();
        assertThat(map.getClass()).isEqualTo(Month.class);
        assertThat(map).isEqualTo(Month.APRIL);

        stringMapper.removeEnumMapper();
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> stringMapper.map(s, Month.class));
    }

    @Test
    void mapStringToLocalDateWithoutMapper() {
        String s = "2018-11-25";

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> StringMapper.getInstance().map(s, LocalDate.class));
    }

    @Test
    void mapStringToLocalDateWithNullMapper() {
        String s = "2018-11-25";

        stringMapper.addCustomMapper(LocalDate.class, null);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> stringMapper.map(s, LocalDate.class));
    }

    @Test
    void mapStringToLocalDateWithMapper() {
        String s = "2018-11-25";

        stringMapper.addCustomMapper(LocalDate.class, LocalDate::parse);

        Object map = stringMapper.map(s, LocalDate.class);
        assertThat(map).isNotNull();
        assertThat(map.getClass()).isEqualTo(LocalDate.class);
        assertThat(map).isEqualTo(LocalDate.of(2018, 11, 25));

        stringMapper.removeCustomMapper(LocalDate.class);
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> stringMapper.map(s, LocalDate.class));
    }

    // endregion

    // region mapString parent/child

    @Test
    void mapStringToParentTestClassWithChildMapper() {
        String s = "test";

        stringMapper.addCustomMapper(ChildTestClass.class, ChildTestClass::new);

        Object map;
        map = stringMapper.map(s, ParentTestClass.class);
        assertThat(map).isNotNull();
        assertThat(map.getClass()).isNotEqualTo(ParentTestClass.class);
        assertThat(map).isEqualTo(new ParentTestClass(s));

        map = stringMapper.map(s, ChildTestClass.class);
        assertThat(map).isNotNull();
        assertThat(map.getClass()).isEqualTo(ChildTestClass.class);
        assertThat(map).isEqualTo(new ChildTestClass(s));
    }

    @Test
    void mapStringToChildTestClassWithParentMapper() {
        String s = "test";

        stringMapper.addCustomMapper(ParentTestClass.class, ParentTestClass::new);

        Object map = stringMapper.map(s, ParentTestClass.class);
        assertThat(map).isNotNull();
        assertThat(map.getClass()).isEqualTo(ParentTestClass.class);
        assertThat(map).isEqualTo(new ParentTestClass(s));

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> stringMapper.map(s, ChildTestClass.class));
    }

    @Test
    void mapStringToClassWithInterfaceMapper() {
        String s = "test";

        stringMapper.addCustomMapper(InterfaceTestClass.class, InterfaceTestClass::new);

        Object map;
        map = stringMapper.map(s, TestInterface.class);
        assertThat(map).isNotNull();
        assertThat(map.getClass()).isNotEqualTo(TestInterface.class);
        assertThat(map).isEqualTo(new InterfaceTestClass(s));

        map = stringMapper.map(s, InterfaceTestClass.class);
        assertThat(map).isNotNull();
        assertThat(map.getClass()).isEqualTo(InterfaceTestClass.class);
        assertThat(map).isEqualTo(new InterfaceTestClass(s));
    }

    @Test
    void mapStringToInterfaceWithClassMapper() {
        String s = "test";

        stringMapper.addCustomMapper(TestInterface.class, InterfaceTestClass::new);

        Object map = stringMapper.map(s, TestInterface.class);
        assertThat(map).isNotNull();
        assertThat(map.getClass()).isNotEqualTo(TestInterface.class);
        assertThat(map).isEqualTo(new InterfaceTestClass(s));

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> stringMapper.map(s, InterfaceTestClass.class));
    }

    // endregion

    @ParameterizedTest
    @MethodSource("getValidMappingArguments")
    void mapValidString(Class<?> type, String s) {
        Object map = StringMapper.getInstance().map(s, type);
        assertThat(map).isNotNull();
        assertThat(map.getClass()).isEqualTo(StringMapper.getObjectInstance(type));
        assertThat(String.valueOf(map)).isEqualTo(s);
    }

    @ParameterizedTest
    @MethodSource("getInvalidMappingArguments")
    void mapInvalidString(Class<?> type, String s) {
        if (type.getSimpleName().equalsIgnoreCase("Boolean") &&
            (type.isPrimitive() || !s.equals("null"))) {
            Object map = StringMapper.getInstance().map(s, type);
            assertThat(String.valueOf(map)).isEqualTo("false");
        } else if (!type.isPrimitive() && s.equals("null")) {
            Object map = StringMapper.getInstance().map(s, type);
            assertThat(String.valueOf(map)).isEqualTo(s);
        } else {
            if (type.isAssignableFrom(Number.class)) {
                assertThatExceptionOfType(NumberFormatException.class)
                    .isThrownBy(() -> StringMapper.getInstance().map(s, type));
            } else {
                assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> StringMapper.getInstance().map(s, type));
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
