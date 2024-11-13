package com.smis.user.domain.valueobject;

import com.smis.common.core.entity.ValueObject;
import com.smis.user.domain.exception.UserDomainException;
import com.smis.user.domain.util.Status;

public class UserStatus extends ValueObject<Status> {
    private final Status status;

    public UserStatus(Status status) {
        if (status == null) {
            throw new UserDomainException("User status is required");
        }
        this.status = status;
    }

    @Override
    public Status getValue() {
        return status;
    }
}
