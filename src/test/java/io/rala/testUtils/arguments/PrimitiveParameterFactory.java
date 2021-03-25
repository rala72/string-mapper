package io.rala.testUtils.arguments;

import java.util.Arrays;
import java.util.stream.Stream;

@SuppressWarnings("unused")
class PrimitiveParameterFactory {
    @SuppressWarnings("SpellCheckingInspection")
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String SPECIAL_LETTERS = "äöüß";

    enum TYPE {
        BOOLEAN, BYTE, CHARACTER,
        SHORT, INTEGER, LONG,
        FLOAT, DOUBLE;

        String getClassName() {
            return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
        }
    }

    private PrimitiveParameterFactory() {
    }

    // region streamOf

    public static Stream<String> validStreamOf(TYPE type) {
        return streamOf(type, true);
    }

    public static Stream<String> invalidStreamOf(TYPE type) {
        return streamOf(type, false);
    }

    public static Stream<String> streamOf(TYPE type, boolean valid) {
        switch (type) {
            case BOOLEAN:
                return valid ? validBooleanStream() : invalidBooleanStream();
            case BYTE:
                return valid ? validByteStream() : invalidByteStream();
            case CHARACTER:
                return valid ? validCharacterStream() : invalidCharacterStream();
            case SHORT:
                return valid ? validShortStream() : invalidShortStream();
            case INTEGER:
                return valid ? validIntegerStream() : invalidIntegerStream();
            case LONG:
                return valid ? validLongStream() : invalidLongStream();
            case FLOAT:
                return valid ? validFloatStream() : invalidFloatStream();
            case DOUBLE:
                return valid ? validDoubleStream() : invalidDoubleStream();
        }
        return Stream.empty();
    }

    // endregion
    // region streamHolderOf

    public static StreamHolder validStreamHolderOf(TYPE type) {
        return streamHolderOf(type, true);
    }

    public static StreamHolder invalidStreamHolderOf(TYPE type) {
        return streamHolderOf(type, false);
    }

    public static StreamHolder streamHolderOf(TYPE type, boolean valid) {
        return StreamHolder.of(type, valid);
    }

    // endregion

    // region valid

    static Stream<String> validBooleanStream() {
        return Stream.of(
            "false", "true"
        );
    }

    static Stream<String> validByteStream() {
        return Stream.of(
            String.valueOf(Byte.MIN_VALUE), "0", String.valueOf(Byte.MAX_VALUE)
        );
    }

    static Stream<String> validCharacterStream() {
        return Stream.of(
            ALPHABET.toLowerCase().split(""),
            ALPHABET.toUpperCase().split(""),
            SPECIAL_LETTERS.toLowerCase().split(""),
            SPECIAL_LETTERS.toUpperCase().split(""),
            new String[]{"\t", "\\", "\"", "#", "+", "*", "?"}
        ).flatMap(Arrays::stream);
    }

    static Stream<String> validShortStream() {
        return Stream.of(
            String.valueOf(Short.MIN_VALUE), "0", String.valueOf(Short.MAX_VALUE)
        );
    }

    static Stream<String> validIntegerStream() {
        return Stream.of(
            String.valueOf(Integer.MIN_VALUE), "0", String.valueOf(Integer.MAX_VALUE)
        );
    }

    static Stream<String> validLongStream() {
        return Stream.of(
            String.valueOf(Long.MIN_VALUE), "0", String.valueOf(Long.MAX_VALUE)
        );
    }

    static Stream<String> validFloatStream() {
        return Stream.of(
            String.valueOf(Float.NEGATIVE_INFINITY),
            String.valueOf(-Float.MIN_VALUE),
            String.valueOf(Float.MIN_VALUE),
            String.valueOf(-Float.MIN_NORMAL),
            String.valueOf(Float.MIN_NORMAL),
            "0.0",
            String.valueOf(-Float.MAX_VALUE),
            String.valueOf(Float.MAX_VALUE),
            String.valueOf(Float.POSITIVE_INFINITY),
            String.valueOf(Float.NaN)
        );
    }

    static Stream<String> validDoubleStream() {
        return Stream.of(
            String.valueOf(Double.NEGATIVE_INFINITY),
            String.valueOf(-Double.MIN_VALUE),
            String.valueOf(Double.MIN_VALUE),
            String.valueOf(-Double.MIN_NORMAL),
            String.valueOf(Double.MIN_NORMAL),
            "0.0", String.valueOf(Double.MAX_VALUE),
            String.valueOf(Double.POSITIVE_INFINITY),
            String.valueOf(Double.NaN)
        );
    }

    // endregion
    // region invalid

    static Stream<String> invalidBooleanStream() {
        return Stream.of(
            "null", "-1"
        );
    }

    static Stream<String> invalidByteStream() {
        return Stream.of(
            "null",
            String.valueOf(Byte.MIN_VALUE - 1),
            String.valueOf(Byte.MAX_VALUE + 1)
        );
    }

    static Stream<String> invalidCharacterStream() {
        return Stream.of(
            "null", "ab"
        );
    }

    static Stream<String> invalidShortStream() {
        return Stream.of(
            "null",
            String.valueOf(Short.MIN_VALUE - 1),
            String.valueOf(Short.MAX_VALUE + 1)
        );
    }

    static Stream<String> invalidIntegerStream() {
        return Stream.of(
            "null",
            String.valueOf(Integer.MIN_VALUE - 1L),
            String.valueOf(Integer.MAX_VALUE + 1L)
        );
    }

    static Stream<String> invalidLongStream() {
        return Stream.of(
            "null"
        );
    }

    static Stream<String> invalidFloatStream() {
        return Stream.of(
            "null"
        );
    }

    static Stream<String> invalidDoubleStream() {
        return Stream.of(
            "null"
        );
    }

    // endregion

    static class StreamHolder {
        private final TYPE type;
        private final Stream<String> stream;

        private StreamHolder(TYPE type, Stream<String> stream) {
            this.type = type;
            this.stream = stream;
        }

        static StreamHolder of(TYPE type, boolean valid) {
            return new StreamHolder(type, streamOf(type, valid));
        }

        public TYPE getType() {
            return type;
        }

        public String getClassName() {
            return getType().getClassName();
        }

        public Stream<String> getStream() {
            return stream;
        }

        @Override
        public String toString() {
            return type.toString();
        }
    }
}
