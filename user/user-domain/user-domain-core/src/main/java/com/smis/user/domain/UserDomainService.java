package com.smis.user.domain;

import com.smis.common.core.valueobject.ExecutionUser;
import com.smis.common.core.valueobject.UserRight;
import com.smis.user.domain.entity.RightGroup;
import com.smis.user.domain.entity.User;
import com.smis.user.domain.event.*;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public interface UserDomainService {
    RightGroupCreatedEvent createRightGroup(@NotNull RightGroup rightGroup, @NotNull ExecutionUser executionUser, Set<UserRight> systemWideRightsManagedByOwner);

    RightGroupUpdatedEvent updateRightGroup(@NotNull RightGroup rightGroup, @NotNull ExecutionUser executionUser, Set<UserRight> systemWideRightsManagedByOwner);

    RightGroupDeletedEvent deleteRightGroup(@NotNull RightGroup rightGroup, @NotNull ExecutionUser executionUser, Set<UserRight> systemWideRightsManagedByOwner);

    RightGroupUpdatedEvent addUserRight(@NotNull RightGroup rightGroup, @NotNull ExecutionUser executionUser, Set<UserRight> systemWideRightsManagedByOwner, @NotNull UserRight userRight);

    RightGroupUpdatedEvent removeUserRight(@NotNull RightGroup rightGroup, @NotNull ExecutionUser executionUser, Set<UserRight> systemWideRightsManagedByOwner, @NotNull UserRight userRight);

    UserCreatedEvent createUser(@NotNull User user, @NotNull ExecutionUser executionUser);

    UserUpdatedEvent updateUser(@NotNull User user, @NotNull ExecutionUser executionUser);

    UserUpdatedEvent updateUserPassword(@NotNull User user, @NotNull ExecutionUser executionUser);

    UserUpdatedEvent addUserRightGroup(@NotNull User user, @NotNull ExecutionUser executionUser, @NotNull RightGroup rightGroup);

    UserDeletedEvent deleteUser(@NotNull User user, @NotNull ExecutionUser executionUser);
}
