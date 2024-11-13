package com.smis.user.domain.valueobject;

import com.smis.common.core.entity.ValueObject;
import com.smis.user.domain.exception.UserDomainException;

public class Username extends ValueObject<String> {
    private final String username;

    public Username(String username) {
        if (username == null || username.isBlank() || username.trim().length() < 3) {
            throw new UserDomainException("Username must be at-least 3 characters");
        }
        this.username = username;
    }

    @Override
    public String getValue() {
        return username;
    }
}
