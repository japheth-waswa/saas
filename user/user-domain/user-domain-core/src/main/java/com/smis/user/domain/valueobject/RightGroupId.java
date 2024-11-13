package com.smis.user.domain.valueobject;

import com.smis.common.core.entity.BaseId;
import com.smis.user.domain.exception.UserDomainException;

import java.util.UUID;

public class RightGroupId extends BaseId<UUID> {
    public RightGroupId(UUID id) {
        super(id);
        if(id == null){
            throw new UserDomainException("Right group id cannot be null");
        }
    }
}
