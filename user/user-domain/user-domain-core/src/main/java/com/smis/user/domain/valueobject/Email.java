package com.smis.user.domain.valueobject;

import com.smis.common.core.entity.ValueObject;
import com.smis.user.domain.exception.UserDomainException;
import org.apache.commons.validator.routines.EmailValidator;

public class Email extends ValueObject<String> {
    private final String email;

    public Email(String email) {
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new UserDomainException("Invalid email");
        }
        this.email = email;
    }

    @Override
    public String getValue() {
        return email;
    }
}
