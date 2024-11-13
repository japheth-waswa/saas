package com.smis.user.domain.ports.input.service;

import com.smis.common.core.valueobject.ExecutionUser;
import com.smis.common.core.valueobject.UserId;
import com.smis.common.core.valueobject.UserType;
import com.smis.user.domain.dto.user.*;
import com.smis.user.domain.valueobject.Names;
import com.smis.user.domain.valueobject.OwnerId;
import com.smis.user.domain.valueobject.Username;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public interface UserApplicationService {
    LoginResponse login(@NotNull @Valid LoginPayload loginPayload);

    UserResponse createUser(@NotNull ExecutionUser executionUser, @NotNull @Valid GenericUserCommand genericUserCommand);

    UserResponse updateUser(@NotNull ExecutionUser executionUser, @NotNull UserId userId, @NotNull @Valid GenericUserCommand genericUserCommand);

    UserResponse updateUserPassword(@NotNull ExecutionUser executionUser, @NotNull UserId userId, @NotNull @Valid PasswordModifyUserCommand passwordModifyUserCommand);

    void deleteUser(@NotNull ExecutionUser executionUser, @NotNull UserId userId);

    UserResponse findUser(@NotNull ExecutionUser executionUser, @NotNull Username username);

    UserListResponse findAllUsers(@NotNull ExecutionUser executionUser, @Min(0) int pageNumber, @Min(1) int pageSize);

    UserListResponse findAllUsersByUserType(@NotNull ExecutionUser executionUser, @NotNull UserType userType, @Min(0) int pageNumber, @Min(1) int pageSize);

    UserListResponse findAllUsersByNames(@NotNull ExecutionUser executionUser, @NotNull Names names, @Min(0) int pageNumber, @Min(1) int pageSize);

    UserListResponse findAllUsersByOwnerId(@NotNull ExecutionUser executionUser, @NotNull OwnerId ownerId, @Min(0) int pageNumber, @Min(1) int pageSize);

    UserListResponse findAllUsersByOwnerIdAndNames(@NotNull ExecutionUser executionUser, @NotNull OwnerId ownerId, @NotNull Names names, @Min(0) int pageNumber, @Min(1) int pageSize);
}
