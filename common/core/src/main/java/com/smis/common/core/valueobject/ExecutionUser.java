package com.smis.common.core.valueobject;

import com.smis.common.core.exception.DomainException;
import lombok.Getter;

@Getter
public class ExecutionUser {
    private final UserId userId;
    private final UserType userType;

    public ExecutionUser(UserId userId, UserType userType) {
        if (userId == null) {
            throw new DomainException("User Id cannot be null");
        }
        if (userType == null) {
            throw new DomainException("User type is required");
        }

        this.userId = userId;
        this.userType = userType;
    }
}
