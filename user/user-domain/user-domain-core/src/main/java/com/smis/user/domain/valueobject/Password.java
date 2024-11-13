package com.smis.user.domain.valueobject;

import com.smis.common.core.entity.ValueObject;

public class Password extends ValueObject<String> {
    private final String password;

    public Password(String password) {
        this.password = password;
    }

    @Override
    public String getValue() {
        return password;
    }
}
