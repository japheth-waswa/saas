package com.smis.common.core.registry;

import com.smis.common.core.valueobject.UserRight;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@Getter
public class OwnerManagedRightsRegistry {
    private final Set<UserRight> ownerManagedRights = new HashSet<>();

    public void registerOwnerManagedRights(Set<UserRight> ownerManagedRights) {
        this.ownerManagedRights.addAll(ownerManagedRights);
    }
}
