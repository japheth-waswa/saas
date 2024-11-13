package com.smis.user.domain.valueobject;

import com.smis.common.core.entity.BaseId;
import com.smis.user.domain.exception.UserDomainException;

import java.util.UUID;

public class OwnerId extends BaseId<UUID> {
    public OwnerId(UUID id) {
        super(id);
        if (id == null) {
            throw new UserDomainException("Owner id is required!");
        }
    }
}
