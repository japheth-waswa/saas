package com.smis.common.core.entity;

public abstract class ValueObject<T> {
    public abstract T getValue();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValueObject<?> that = (ValueObject<?>) o;
        return getValue().equals(that.getValue());
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }
}
