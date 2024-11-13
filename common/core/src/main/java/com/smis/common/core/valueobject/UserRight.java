package com.smis.common.core.valueobject;

import com.smis.common.core.entity.ValueObject;
import com.smis.common.core.util.Right;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserRight extends ValueObject<Right> {
    private final Right right;

    @Override
    public Right getValue() {
        return right;
    }
}
