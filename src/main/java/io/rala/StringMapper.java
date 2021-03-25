package io.rala;

/**
 * maps a string to an object based on specified class
 */
public class StringMapper {
    /**
     * creates basic {@link StringMapper}
     */
    public StringMapper() {
    }

    /**
     * @param type to get object from
     * @return converted object - or request string if not supported
     * @throws IllegalArgumentException if target class is {@code char} and length is not {@code 1}
     */
    public Object map(String string, Class<?> type) {
        if (string == null) string = "null";
        if (!type.isPrimitive() && string.equals("null")) return null;
        if (boolean.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type)) {
            return Boolean.parseBoolean(string);
        }
        if (byte.class.isAssignableFrom(type) || Byte.class.isAssignableFrom(type)) {
            return Byte.parseByte(string);
        }
        if (char.class.isAssignableFrom(type) || Character.class.isAssignableFrom(type)) {
            if (string.length() != 1)
                throw new IllegalArgumentException("String is no character: " + string);
            return string.charAt(0);
        }
        if (short.class.isAssignableFrom(type) || Short.class.isAssignableFrom(type)) {
            return Short.parseShort(string);
        }
        if (int.class.isAssignableFrom(type) || Integer.class.isAssignableFrom(type)) {
            return Integer.parseInt(string);
        }
        if (long.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type)) {
            return Long.parseLong(string);
        }
        if (float.class.isAssignableFrom(type) || Float.class.isAssignableFrom(type)) {
            return Float.parseFloat(string);
        }
        if (double.class.isAssignableFrom(type) || Double.class.isAssignableFrom(type)) {
            return Double.parseDouble(string);
        }
        return string;
    }
}
