package com.smis.common.core.event;

import com.smis.common.core.valueobject.ExecutionUser;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@AllArgsConstructor
@Getter
public class DomainEvent<T> {
    private T payload;
    private Instant createdAt;
    private ExecutionUser executionUser;

    private DomainEvent(){}
}
