package io.rala.testUtils.arguments;

import org.junit.jupiter.params.provider.Arguments;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ParameterArgumentsStreamFactory {
    private ParameterArgumentsStreamFactory() {
    }

    public static Stream<Arguments> validMapping() {
        return Arrays.stream(PrimitiveParameterFactory.TYPE.values())
            .map(PrimitiveParameterFactory::validStreamHolderOf)
            .flatMap(streamHolder -> createMappingParameterArgumentsStream(
                streamHolder.getClassName(),
                streamHolder.getStream().toArray(String[]::new)
            ));
    }

    public static Stream<Arguments> invalidMapping() {
        return Arrays.stream(PrimitiveParameterFactory.TYPE.values())
            .map(PrimitiveParameterFactory::invalidStreamHolderOf)
            .flatMap(streamHolder -> createMappingParameterArgumentsStream(
                streamHolder.getClassName(),
                streamHolder.getStream().toArray(String[]::new)
            ));
    }

    private static Stream<Arguments> createMappingParameterArgumentsStream(
        String name, String... strings
    ) {
        Class<?> type;
        try {
            type = Class.forName(String.format("java.lang.%s", name));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return Stream.empty();
        }
        List<Arguments> list = new ArrayList<>();
        for (String param : strings) {
            list.add(Arguments.of(
                type, param
            ));
            Class<?> primitiveClass;
            try {
                Field primitiveField = type.getField("TYPE");
                primitiveClass = (Class<?>) primitiveField.get(null);
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
                continue;
            }
            list.add(Arguments.of(primitiveClass, param));
        }
        return list.stream();
    }
}
