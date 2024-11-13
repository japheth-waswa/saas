package com.smis.common.core.entity;

public abstract class BaseId<T> {
    private final T id;

    public BaseId(T id) {
        this.id = id;
    }

    public T getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseId<?> baseId = (BaseId<?>) o;
        return id.equals(baseId.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
