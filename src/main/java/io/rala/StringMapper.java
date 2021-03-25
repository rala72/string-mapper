package io.rala;

/**
 * maps a string to an object based on specified class
 */
public class StringMapper {
    private final String string;

    /**
     * @param string string to map
     */
    public StringMapper(String string) {
        this.string = string != null ? string : "null";
    }

    /**
     * @return request string
     */
    public String getString() {
        return string;
    }

    /**
     * @param type to get object from
     * @return converted object - or request string if not supported
     * @throws IllegalArgumentException if target class is {@code char} and length is not {@code 1}
     */
    public Object map(Class<?> type) {
        if (!type.isPrimitive() && getString().equals("null")) return null;
        if (boolean.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type)) {
            return Boolean.parseBoolean(getString());
        }
        if (byte.class.isAssignableFrom(type) || Byte.class.isAssignableFrom(type)) {
            return Byte.parseByte(getString());
        }
        if (char.class.isAssignableFrom(type) || Character.class.isAssignableFrom(type)) {
            if (getString().length() != 1)
                throw new IllegalArgumentException("String is no character: " + getString());
            return getString().charAt(0);
        }
        if (short.class.isAssignableFrom(type) || Short.class.isAssignableFrom(type)) {
            return Short.parseShort(getString());
        }
        if (int.class.isAssignableFrom(type) || Integer.class.isAssignableFrom(type)) {
            return Integer.parseInt(getString());
        }
        if (long.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type)) {
            return Long.parseLong(getString());
        }
        if (float.class.isAssignableFrom(type) || Float.class.isAssignableFrom(type)) {
            return Float.parseFloat(getString());
        }
        if (double.class.isAssignableFrom(type) || Double.class.isAssignableFrom(type)) {
            return Double.parseDouble(getString());
        }
        return getString();
    }

    @Override
    public String toString() {
        return getString();
    }
}
