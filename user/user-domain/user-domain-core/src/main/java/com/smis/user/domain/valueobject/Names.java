package com.smis.user.domain.valueobject;

import com.smis.common.core.entity.ValueObject;
import com.smis.user.domain.exception.UserDomainException;

public class Names extends ValueObject<String> {
    private final String names;

    public Names(String names) {
        if (names == null || names.isBlank() || names.trim().length() < 3) {
            throw new UserDomainException("Names must be at-least 3 characters");
        }
        this.names = names;
    }

    @Override
    public String getValue() {
        return names;
    }
}
