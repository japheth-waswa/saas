package com.smis.user.domain.adapters;

import com.smis.common.core.valueobject.ExecutionUser;
import com.smis.common.core.valueobject.UserId;
import com.smis.common.core.valueobject.UserType;
import com.smis.user.domain.command.UserCommandHandler;
import com.smis.user.domain.dto.user.*;
import com.smis.user.domain.ports.input.service.UserApplicationService;
import com.smis.user.domain.valueobject.Names;
import com.smis.user.domain.valueobject.OwnerId;
import com.smis.user.domain.valueobject.Username;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@AllArgsConstructor
public class UserApplicationServiceImpl implements UserApplicationService {
    private final UserCommandHandler userCommandHandler;

    @Override
    public LoginResponse login(LoginPayload loginPayload) {
        return userCommandHandler.login(loginPayload);
    }

    @Override
    public UserResponse createUser(ExecutionUser executionUser, GenericUserCommand genericUserCommand) {
        return userCommandHandler.createUser(executionUser, genericUserCommand);
    }

    @Override
    public UserResponse updateUser(ExecutionUser executionUser, UserId userId, GenericUserCommand genericUserCommand) {
        return userCommandHandler.updateUser(executionUser, userId, genericUserCommand);
    }

    @Override
    public UserResponse updateUserPassword(ExecutionUser executionUser, UserId userId, PasswordModifyUserCommand passwordModifyUserCommand) {
        return userCommandHandler.updateUserPassword(executionUser, userId, passwordModifyUserCommand);
    }

    @Override
    public void deleteUser(ExecutionUser executionUser, UserId userId) {
        userCommandHandler.deleteUser(executionUser, userId);
    }

    @Override
    public UserResponse findUser(ExecutionUser executionUser, Username username) {
        return userCommandHandler.findUser(executionUser, username);
    }

    @Override
    public UserListResponse findAllUsers(ExecutionUser executionUser, int pageNumber, int pageSize) {
        return userCommandHandler.findAllUsers(executionUser, pageNumber, pageSize);
    }

    @Override
    public UserListResponse findAllUsersByUserType(ExecutionUser executionUser, UserType userType, int pageNumber, int pageSize) {
        return userCommandHandler.findAllUsersByUserType(executionUser, userType, pageNumber, pageSize);
    }

    @Override
    public UserListResponse findAllUsersByNames(ExecutionUser executionUser, Names names, int pageNumber, int pageSize) {
        return userCommandHandler.findAllUsersByNames(executionUser, names, pageNumber, pageSize);
    }

    @Override
    public UserListResponse findAllUsersByOwnerId(ExecutionUser executionUser, OwnerId ownerId, int pageNumber, int pageSize) {
        return userCommandHandler.findAllUsersByOwnerId(executionUser, ownerId, pageNumber, pageSize);
    }

    @Override
    public UserListResponse findAllUsersByOwnerIdAndNames(ExecutionUser executionUser, OwnerId ownerId, Names names, int pageNumber, int pageSize) {
        return userCommandHandler.findAllUsersByOwnerIdAndNames(executionUser, ownerId, names, pageNumber, pageSize);
    }
}
