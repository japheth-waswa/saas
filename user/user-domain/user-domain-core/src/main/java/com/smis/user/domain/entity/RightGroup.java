package com.smis.user.domain.entity;

import com.smis.common.core.entity.BaseEntity;
import com.smis.common.core.util.Role;
import com.smis.common.core.valueobject.ExecutionUser;
import com.smis.common.core.valueobject.UserId;
import com.smis.common.core.valueobject.UserRight;
import com.smis.common.core.valueobject.UserType;
import com.smis.user.domain.exception.UserDomainException;
import com.smis.user.domain.valueobject.RightGroupId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = false)
public class RightGroup extends BaseEntity<RightGroupId> {
    public static final String USER_NOT_ALLOWED_TO_MODIFY_RIGHT_GROUP = "User not allowed to modify right group";
    private final String name;
    private List<UserRight> rights;
    private final UserId creatorUserId;
    private final UserType creatorUserType;

    private RightGroup(Builder builder) {
        super.setId(builder.rightGroupId);
        name = builder.name;
        rights = builder.rights;
        creatorUserId = builder.creatorUserId;
        creatorUserType = builder.creatorUserType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private RightGroupId rightGroupId;
        private String name;
        private List<UserRight> rights;
        private UserId creatorUserId;
        private UserType creatorUserType;

        private Builder() {
        }

        public Builder rightGroupId(RightGroupId val) {
            rightGroupId = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder rights(List<UserRight> val) {
            rights = val;
            return this;
        }

        public Builder creatorUserId(UserId val) {
            creatorUserId = val;
            return this;
        }

        public Builder creatorUserType(UserType val) {
            creatorUserType = val;
            return this;
        }

        public RightGroup build() {
            return new RightGroup(this);
        }
    }


    public void addUserRight(ExecutionUser executionUser, Set<UserRight> systemWideRightsManagedByOwner, UserRight userRight) {
        checkIfExecutionUserAllowed(executionUser, systemWideRightsManagedByOwner);
        if (rights == null) {
            rights = List.of();
        }
        if (userRight != null && !rights.contains(userRight)) {
            List<UserRight> mutableRights = new ArrayList<>(rights);
            mutableRights.add(userRight);
            rights = List.copyOf(mutableRights);
        }
    }

    public void removeUserRight(ExecutionUser executionUser, Set<UserRight> systemWideRightsManagedByOwner, UserRight userRight) {
        checkIfExecutionUserAllowed(executionUser, systemWideRightsManagedByOwner);
        if (rights == null) {
            return;
        }
        if (rights.contains(userRight)) {
            List<UserRight> mutableRights = new ArrayList<>(rights);
            mutableRights.remove(userRight);
            rights = List.copyOf(mutableRights);
        }
    }

    public void create(ExecutionUser executionUser, Set<UserRight> systemWideRightsManagedByOwner) {
        checkIfExecutionUserAllowed(executionUser, systemWideRightsManagedByOwner);
        if (name == null || name.isBlank()) {
            throw new UserDomainException("Right group name is required");
        }
        if (rights == null || rights.isEmpty()) {
            throw new UserDomainException("Right group must have at least one right");
        }
    }

    public void update(ExecutionUser executionUser, Set<UserRight> systemWideRightsManagedByOwner) {
        checkIfExecutionUserAllowed(executionUser, systemWideRightsManagedByOwner);
        if (name == null || name.isBlank()) {
            throw new UserDomainException("Right group name is required");
        }
        if (rights == null || rights.isEmpty()) {
            throw new UserDomainException("Right group must have at least one right");
        }
    }

    public void delete(ExecutionUser executionUser, Set<UserRight> systemWideRightsManagedByOwner) {
        checkIfExecutionUserAllowed(executionUser, systemWideRightsManagedByOwner);
    }

    private void checkIfExecutionUserAllowed(ExecutionUser executionUser, Set<UserRight> systemWideRightsManagedByOwner) {
        if (isExecutionUserInvalid(executionUser)) {
            throw new UserDomainException(USER_NOT_ALLOWED_TO_MODIFY_RIGHT_GROUP);
        }

        if (Role.OWNER.equals(executionUser.getUserType().getValue())) {
            validateUserRightsForOwner(systemWideRightsManagedByOwner);
        }
        checkWhetherExecutionUserMatchesCreator(executionUser);
    }

    private boolean isExecutionUserInvalid(ExecutionUser executionUser) {
        return executionUser == null || !isUserTypeAllowed(executionUser.getUserType().getValue())
                || (creatorUserType != null && executionUser.getUserType().getValue().equals(Role.OWNER) && !creatorUserType.getValue().equals(Role.OWNER));
    }

    private boolean isUserTypeAllowed(Role userType) {
        return Role.SU.equals(userType) || Role.ADMIN.equals(userType) || Role.OWNER.equals(userType);
    }

    private void validateUserRightsForOwner(Set<UserRight> systemWideRightsManagedByOwner) {
        Assert.notNull(systemWideRightsManagedByOwner, "System wide rights managed by owner must be present");
        Assert.notEmpty(systemWideRightsManagedByOwner, "System wide rights managed by owner must not be empty");
        Assert.notNull(rights, "User rights must be present");
        var foundNonOwnerManagedRight = rights.stream()
                .anyMatch(userRight -> userRight != null && !systemWideRightsManagedByOwner.contains(userRight));
        if (foundNonOwnerManagedRight) {
            throw new UserDomainException("Some rights cannot be manage by " + Role.OWNER);
        }
    }

    private void checkWhetherExecutionUserMatchesCreator(ExecutionUser executionUser) {
        if (Role.OWNER.equals(executionUser.getUserType().getValue())) {
            executionUserMustBeSameAsCreator(executionUser);
            return;
        }

        if (Role.ADMIN.equals(executionUser.getUserType().getValue())
                && !Role.ADMIN.equals(creatorUserType.getValue())) {
            throw new UserDomainException(USER_NOT_ALLOWED_TO_MODIFY_RIGHT_GROUP);
        }
    }

    private void executionUserMustBeSameAsCreator(ExecutionUser executionUser) {
        if (!executionUser.getUserId().equals(creatorUserId)) {
            throw new UserDomainException(USER_NOT_ALLOWED_TO_MODIFY_RIGHT_GROUP);
        }
    }
}
