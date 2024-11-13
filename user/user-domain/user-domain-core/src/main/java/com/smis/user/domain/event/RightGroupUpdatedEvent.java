package com.smis.user.domain.event;

import com.smis.common.core.event.DomainEvent;
import com.smis.common.core.valueobject.ExecutionUser;
import com.smis.user.domain.entity.RightGroup;

import java.time.Instant;

public class RightGroupUpdatedEvent extends DomainEvent<RightGroup> {
    public RightGroupUpdatedEvent(RightGroup payload, Instant createdAt, ExecutionUser executionUser) {
        super(payload, createdAt, executionUser);
    }
}
