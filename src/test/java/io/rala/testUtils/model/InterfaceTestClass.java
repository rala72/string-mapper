package io.rala.testUtils.model;

import java.util.Objects;

public class InterfaceTestClass implements TestInterface {
    private final String string;

    public InterfaceTestClass(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InterfaceTestClass)) return false;
        InterfaceTestClass that = (InterfaceTestClass) o;
        return Objects.equals(getString(), that.getString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getString());
    }

    @Override
    public String toString() {
        return "InterfaceTestClass{" +
            "string='" + getString() + '\'' +
            '}';
    }
}
