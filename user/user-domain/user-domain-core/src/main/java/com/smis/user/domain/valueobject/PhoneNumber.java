package com.smis.user.domain.valueobject;

import com.smis.common.core.entity.ValueObject;
import com.smis.user.domain.exception.UserDomainException;

public class PhoneNumber extends ValueObject<Long> {
    private final long phoneNumber;

    public PhoneNumber(long phoneNumber) {
        if (String.valueOf(Math.abs(phoneNumber)).length() != 12) {
            throw new UserDomainException("Phone number must be 12 characters and without special characters");
        }
        this.phoneNumber = phoneNumber;
    }

    @Override
    public Long getValue() {
        return phoneNumber;
    }
}
