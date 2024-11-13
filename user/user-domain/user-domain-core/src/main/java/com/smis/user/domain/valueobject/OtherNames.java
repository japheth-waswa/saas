package com.smis.user.domain.valueobject;

import com.smis.common.core.entity.ValueObject;
import com.smis.user.domain.exception.UserDomainException;

public class OtherNames extends ValueObject<String> {
    private final String name;

    public OtherNames(String name) {
        if (name == null || name.isBlank() || name.trim().length() < 3) {
            throw new UserDomainException("Other names must be at-least 3 characters");
        }
        this.name = name;
    }

    @Override
    public String getValue() {
        return name;
    }
}
