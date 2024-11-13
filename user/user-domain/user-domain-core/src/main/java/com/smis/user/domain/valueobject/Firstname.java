package com.smis.user.domain.valueobject;

import com.smis.common.core.entity.ValueObject;
import com.smis.user.domain.exception.UserDomainException;

public class Firstname extends ValueObject<String> {
    private final String name;

    public Firstname(String name) {
        if (name == null || name.isBlank() || name.trim().length() < 3) {
            throw new UserDomainException("First name must be at-least 3 characters");
        }
        this.name = name;
    }

    @Override
    public String getValue() {
        return name;
    }
}
