package com.smis.common.core.valueobject;

import com.smis.common.core.entity.ValueObject;
import com.smis.common.core.exception.DomainException;
import com.smis.common.core.util.Role;

public class UserType extends ValueObject<Role> {
    private final Role role;

    public UserType(Role role) {
        if (role == null) {
            throw new DomainException("Role is required");
        }
        this.role = role;
    }

    @Override
    public Role getValue() {
        return role;
    }
}
