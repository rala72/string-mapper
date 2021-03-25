package io.rala.testUtils.model;

import java.util.Objects;

public class ParentTestClass {
    private final String string;

    public ParentTestClass(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParentTestClass)) return false;
        ParentTestClass that = (ParentTestClass) o;
        return Objects.equals(getString(), that.getString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getString());
    }

    @Override
    public String toString() {
        return "ParentTestClass{" +
            "string='" + getString() + '\'' +
            '}';
    }
}
