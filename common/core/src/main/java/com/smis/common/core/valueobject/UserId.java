package com.smis.common.core.valueobject;

import com.smis.common.core.entity.BaseId;
import com.smis.common.core.exception.DomainException;

import java.util.UUID;

public class UserId extends BaseId<UUID> {
    public UserId(UUID id) {
        super(id);
        if (id == null) {
            throw new DomainException("User id is required!");
        }
    }
}
