package io.rala;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * maps a string to an object based on specified class
 *
 * @author rala<br>
 * <a href="mailto:code@rala.io">code@rala.io</a><br>
 * <a href="https://www.rala.io">www.rala.io</a>
 * @version 1.0.2
 */
public class StringMapper {
    private static final Map<Class<?>, Class<?>> WRAPPER_TYPE_MAP = new HashMap<>();
    private static StringMapper instance;
    private final Map<Class<?>, Function<String, ?>> mapperMap = new HashMap<>();

    static {
        WRAPPER_TYPE_MAP.put(boolean.class, Boolean.class);
        WRAPPER_TYPE_MAP.put(byte.class, Byte.class);
        WRAPPER_TYPE_MAP.put(char.class, Character.class);
        WRAPPER_TYPE_MAP.put(short.class, Short.class);
        WRAPPER_TYPE_MAP.put(int.class, Integer.class);
        WRAPPER_TYPE_MAP.put(long.class, Long.class);
        WRAPPER_TYPE_MAP.put(float.class, Float.class);
        WRAPPER_TYPE_MAP.put(double.class, Double.class);
        WRAPPER_TYPE_MAP.put(void.class, Void.class);
    }

    /**
     * creates basic {@link StringMapper}
     *
     * @since 1.0.0
     */
    public StringMapper() {
    }

    /**
     * @param type   type of mapper
     * @param mapper custom mapper to consider
     * @param <T>    requested type
     * @param <R>    result type (may be subclass of {@code T})
     * @since 1.0.0
     */
    public <T, R extends T> void addCustomMapper(
        @NotNull Class<T> type, @Nullable Function<String, R> mapper
    ) {
        mapperMap.put(type, mapper);
    }

    /**
     * @param type type of mapper
     * @since 1.0.0
     */
    public void removeCustomMapper(@NotNull Class<?> type) {
        mapperMap.remove(type);
    }

    /**
     * if multiple mapper apply to a specific class (without a own mapper)
     * it is undefined which one is chosen
     *
     * @param string string to map
     * @param type   to get object from
     * @param <T>    requested type
     * @return converted object - always in object form or
     * {@code null} if string is either {@code null} or {@code "null"} and
     * {@code type} is not primitive
     * @throws IllegalArgumentException if class not supported
     * @throws IllegalArgumentException if target class is {@code char} and length is not {@code 1}
     * @see #mapPrimitive(String, Class)
     * @see #getObjectInstance(Class)
     * @since 1.0.0
     */
    @Nullable
    public <T> T map(@Nullable String string, @NotNull Class<T> type) {
        if (string == null) string = "null";
        if (!type.isPrimitive() && string.equals("null")) return null;
        T t = mapPrimitive(string, type);
        if (t != null) return t;

        Function<String, ?> mapper = mapperMap.getOrDefault(type, null);
        if (mapper == null)
            for (Class<?> aClass : mapperMap.keySet()) {
                if (!isSupported(type, aClass)) continue;
                mapper = mapperMap.getOrDefault(aClass, null);
                if (mapper != null) break;
            }
        if (mapper != null) return type.cast(mapper.apply(string));
        throw new IllegalArgumentException(type.getName());
    }

    /**
     * @param string string to map
     * @param type   to get object from
     * @param <T>    requested type
     * @return converted object (in object form) - or {@code null} if not supported
     * @throws IllegalArgumentException if target class is {@code char} and length is not {@code 1}
     * @see #getObjectInstance(Class)
     * @since 1.0.0
     */
    @Nullable
    protected <T> T mapPrimitive(@NotNull String string, @NotNull Class<T> type) {
        Object result;
        if (String.class.isAssignableFrom(type))
            result = string;
        else if (boolean.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type))
            result = Boolean.parseBoolean(string);
        else if (byte.class.isAssignableFrom(type) || Byte.class.isAssignableFrom(type))
            result = Byte.parseByte(string);
        else if (char.class.isAssignableFrom(type) || Character.class.isAssignableFrom(type))
            if (string.length() == 1)
                result = string.charAt(0);
            else throw new IllegalArgumentException("String is no character: " + string);
        else if (short.class.isAssignableFrom(type) || Short.class.isAssignableFrom(type))
            result = Short.parseShort(string);
        else if (int.class.isAssignableFrom(type) || Integer.class.isAssignableFrom(type))
            result = Integer.parseInt(string);
        else if (long.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type))
            result = Long.parseLong(string);
        else if (float.class.isAssignableFrom(type) || Float.class.isAssignableFrom(type))
            result = Float.parseFloat(string);
        else if (double.class.isAssignableFrom(type) || Double.class.isAssignableFrom(type))
            result = Double.parseDouble(string);
        else return null;
        if (type.isPrimitive()) // checked throws ClassCastException
            //noinspection unchecked
            return (T) getObjectInstance(type).cast(result);
        return type.cast(result);
    }

    /**
     * @param expected expected class
     * @param type     type to check
     * @return {@code true} if {@code expected} is assignable to {@code type}
     * @since 1.0.1
     */
    protected boolean isSupported(@NotNull Class<?> expected, @NotNull Class<?> type) {
        Class<?> current = type;
        do {
            if (expected.equals(current)) return true;
            for (Class<?> anInterface : current.getInterfaces())
                if (expected.equals(anInterface))
                    return true;
        } while ((current = current.getSuperclass()) != null);
        return false;
    }

    /**
     * @return default instance of {@link StringMapper}
     * @since 1.0.0
     */
    @NotNull
    public static StringMapper getInstance() {
        return instance == null ? instance = new StringMapper() : instance;
    }

    /**
     * @param type type to try to get primitive instance
     * @return object instance or {@code type}
     * @since 1.0.0
     */
    @Nullable
    @Contract("null -> null; !null -> !null")
    public static Class<?> getObjectInstance(@Nullable Class<?> type) {
        return WRAPPER_TYPE_MAP.getOrDefault(type, type);
    }
}
