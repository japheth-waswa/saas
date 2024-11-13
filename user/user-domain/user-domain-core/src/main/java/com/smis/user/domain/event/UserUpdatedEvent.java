package com.smis.user.domain.event;

import com.smis.common.core.event.DomainEvent;
import com.smis.common.core.valueobject.ExecutionUser;
import com.smis.user.domain.entity.User;

import java.time.Instant;

public class UserUpdatedEvent extends DomainEvent<User> {
    public UserUpdatedEvent(User payload, Instant createdAt, ExecutionUser executionUser) {
        super(payload, createdAt, executionUser);
    }
}
