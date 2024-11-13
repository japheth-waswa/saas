package com.smis.user.domain;

import com.smis.common.core.valueobject.ExecutionUser;
import com.smis.common.core.valueobject.UserRight;
import com.smis.user.domain.entity.RightGroup;
import com.smis.user.domain.entity.User;
import com.smis.user.domain.event.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.Set;

@Slf4j
@Validated
public class UserDomainServiceImpl implements UserDomainService {
    @Override
    public RightGroupCreatedEvent createRightGroup(RightGroup rightGroup, ExecutionUser executionUser, Set<UserRight> systemWideRightsManagedByOwner) {
        rightGroup.create(executionUser,systemWideRightsManagedByOwner);
        log.info("Right group payload validated for creation: {}", rightGroup);
        return new RightGroupCreatedEvent(rightGroup, Instant.now(), executionUser);
    }

    @Override
    public RightGroupUpdatedEvent updateRightGroup(RightGroup rightGroup, ExecutionUser executionUser, Set<UserRight> systemWideRightsManagedByOwner) {
        rightGroup.update(executionUser,systemWideRightsManagedByOwner);
        log.info("Right group payload validated for update: {}", rightGroup);
        return new RightGroupUpdatedEvent(rightGroup, Instant.now(), executionUser);
    }

    @Override
    public RightGroupDeletedEvent deleteRightGroup(RightGroup rightGroup, ExecutionUser executionUser, Set<UserRight> systemWideRightsManagedByOwner) {
        rightGroup.delete(executionUser,systemWideRightsManagedByOwner);
        log.info("Right group payload validated for deletion: {}", rightGroup);
        return new RightGroupDeletedEvent(rightGroup, Instant.now(), executionUser);
    }

    @Override
    public RightGroupUpdatedEvent addUserRight(RightGroup rightGroup, ExecutionUser executionUser, Set<UserRight> systemWideRightsManagedByOwner, UserRight userRight) {
        rightGroup.addUserRight(executionUser,systemWideRightsManagedByOwner, userRight);
        log.info("Right addition: {} to group:{} validated", userRight, rightGroup);
        return new RightGroupUpdatedEvent(rightGroup, Instant.now(), executionUser);
    }

    @Override
    public RightGroupUpdatedEvent removeUserRight(RightGroup rightGroup, ExecutionUser executionUser, Set<UserRight> systemWideRightsManagedByOwner, UserRight userRight) {
        rightGroup.removeUserRight(executionUser,systemWideRightsManagedByOwner, userRight);
        log.info("Right removal:{} from group: {} validated", userRight, rightGroup);
        return new RightGroupUpdatedEvent(rightGroup, Instant.now(), executionUser);
    }

    @Override
    public UserCreatedEvent createUser(User user, ExecutionUser executionUser) {
        user.create(executionUser);
        log.info("User creation validated for payload: {}", user);
        return new UserCreatedEvent(user, Instant.now(), executionUser);
    }

    @Override
    public UserUpdatedEvent updateUser(User user, ExecutionUser executionUser) {
        user.update(executionUser);
        log.info("User update validated for payload: {}", user);
        return new UserUpdatedEvent(user, Instant.now(), executionUser);
    }

    @Override
    public UserUpdatedEvent updateUserPassword(User user, ExecutionUser executionUser) {
        user.updatePassword(executionUser);
        log.info("User password update validated for payload: {}", user);
        return new UserUpdatedEvent(user, Instant.now(), executionUser);
    }

    @Override
    public UserUpdatedEvent addUserRightGroup(User user, ExecutionUser executionUser, RightGroup rightGroup) {
        user.addRightGroup(rightGroup);
        log.info("User right group addition: {} validated for payload: {} ", rightGroup, user);
        return new UserUpdatedEvent(user, Instant.now(), executionUser);
    }

    @Override
    public UserDeletedEvent deleteUser(User user, ExecutionUser executionUser) {
        user.delete(executionUser);
        log.info("User deletion validated for payload: {}", user);
        return new UserDeletedEvent(user, Instant.now(), executionUser);
    }
}
