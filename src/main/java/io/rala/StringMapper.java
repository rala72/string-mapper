package io.rala;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * maps a string to an object based on specified class
 */
public class StringMapper {
    private static StringMapper instance;
    private final Map<Class<?>, Function<String, Object>> mapperMap = new HashMap<>();

    /**
     * creates basic {@link StringMapper}
     */
    public StringMapper() {
    }

    /**
     * @param type   type of mapper
     * @param mapper custom mapper to consider
     */
    public void addCustomMapper(Class<?> type, Function<String, Object> mapper) {
        mapperMap.put(type, mapper);
    }

    /**
     * @param type type of mapper
     */
    public void removeCustomMapper(Class<?> type) {
        mapperMap.remove(type);
    }

    /**
     * @param type to get object from
     * @return converted object
     * @throws IllegalArgumentException if class not supported
     * @throws IllegalArgumentException if target class is {@code char} and length is not {@code 1}
     * @see #mapPrimitive(String, Class)
     */
    public Object map(String string, Class<?> type) {
        if (string == null) string = "null";
        if (!type.isPrimitive() && string.equals("null")) return null;
        Object o = mapPrimitive(string, type);
        if (o != null) return o;

        Class<?> current = type;
        Function<String, Object> mapper;
        do {
            mapper = mapperMap.getOrDefault(current, null);
            if (mapper != null) break;
            for (Class<?> anInterface : current.getInterfaces()) {
                mapper = mapperMap.getOrDefault(anInterface, null);
                if (mapper != null) break;
            }
        } while (mapper == null && (current = current.getSuperclass()) != null);
        if (mapper != null) return mapper.apply(string);
        throw new IllegalArgumentException(type.getName());
    }

    /**
     * @param type to get object from
     * @return converted object - or {@code null} if not supported
     * @throws IllegalArgumentException if target class is {@code char} and length is not {@code 1}
     */
    protected Object mapPrimitive(String string, Class<?> type) {
        if (String.class.isAssignableFrom(type))
            return string;
        if (boolean.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type))
            return Boolean.parseBoolean(string);
        if (byte.class.isAssignableFrom(type) || Byte.class.isAssignableFrom(type))
            return Byte.parseByte(string);
        if (char.class.isAssignableFrom(type) || Character.class.isAssignableFrom(type))
            if (string.length() == 1)
                return string.charAt(0);
            else throw new IllegalArgumentException("String is no character: " + string);
        if (short.class.isAssignableFrom(type) || Short.class.isAssignableFrom(type))
            return Short.parseShort(string);
        if (int.class.isAssignableFrom(type) || Integer.class.isAssignableFrom(type))
            return Integer.parseInt(string);
        if (long.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type))
            return Long.parseLong(string);
        if (float.class.isAssignableFrom(type) || Float.class.isAssignableFrom(type))
            return Float.parseFloat(string);
        if (double.class.isAssignableFrom(type) || Double.class.isAssignableFrom(type))
            return Double.parseDouble(string);
        return null;
    }

    /**
     * @return default instance of {@link StringMapper}
     */
    public static StringMapper getInstance() {
        return instance == null ? instance = new StringMapper() : instance;
    }
}
