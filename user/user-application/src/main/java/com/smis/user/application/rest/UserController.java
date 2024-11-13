package com.smis.user.application.rest;

import com.smis.common.core.dto.ApiResponse;
import com.smis.common.core.dto.LoggedInUser;
import com.smis.common.core.util.Helpers;
import com.smis.common.core.util.Role;
import com.smis.common.core.valueobject.ExecutionUser;
import com.smis.common.core.valueobject.UserId;
import com.smis.common.core.valueobject.UserType;
import com.smis.security.util.SecurityHelper;
import com.smis.user.domain.dto.user.GenericUserCommand;
import com.smis.user.domain.dto.user.PasswordModifyUserCommand;
import com.smis.user.domain.dto.user.UserListResponse;
import com.smis.user.domain.dto.user.UserResponse;
import com.smis.user.domain.ports.input.service.UserApplicationService;
import com.smis.user.domain.valueobject.Names;
import com.smis.user.domain.valueobject.OwnerId;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserApplicationService userApplicationService;
    private final SecurityHelper securityHelper;

    @GetMapping
    @PreAuthorize("hasAuthority('SU') or @securityHelper.hasAuthorityAndAnyRight(authentication, 'ADMIN', 'USER_READ') or @securityHelper.hasAuthorityAndAnyRight(authentication, 'OWNER', 'USER_READ')")
    ResponseEntity<ApiResponse<UserListResponse>> fetchUsers(@AuthenticationPrincipal Jwt jwt, @RequestParam int pageNumber, @RequestParam int pageSize,
                                                             @RequestParam(required = false) Role userType, @RequestParam(required = false) String names) {

        LoggedInUser loggedInUser = securityHelper.getJwtUserClaim(jwt, LoggedInUser.class);
        ExecutionUser executionUser = Helpers.buildExecutionUser(loggedInUser);
        UserListResponse userListResponse;

        if (loggedInUser.userType().equals(Role.OWNER)) {
            if (names != null && !names.isBlank()) {
                userListResponse = userApplicationService.findAllUsersByOwnerIdAndNames(executionUser, new OwnerId(executionUser.getUserId().getId()), new Names(names), pageNumber, pageSize);
            } else {
                userListResponse = userApplicationService.findAllUsersByOwnerId(executionUser, new OwnerId(executionUser.getUserId().getId()), pageNumber, pageSize);
            }
        } else if (loggedInUser.userType().equals(Role.ADMIN)) {
            userListResponse = userApplicationService.findAllUsersByUserType(executionUser, new UserType(Role.OWNER), pageNumber, pageSize);
        } else if (names != null && !names.isBlank()) {
            userListResponse = userApplicationService.findAllUsersByNames(executionUser, new Names(names), pageNumber, pageSize);
        } else if (userType != null) {
            userListResponse = userApplicationService.findAllUsersByUserType(executionUser, new UserType(userType), pageNumber, pageSize);
        } else {
            userListResponse = userApplicationService.findAllUsers(executionUser, pageNumber, pageSize);
        }
        return ResponseEntity.ok(new ApiResponse<>(null, userListResponse));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SU') or @securityHelper.hasAuthorityAndAnyRight(authentication, 'ADMIN', 'USER_CREATE') or @securityHelper.hasAuthorityAndAnyRight(authentication, 'OWNER', 'USER_CREATE')")
    ResponseEntity<ApiResponse<UserResponse>> createUser(@AuthenticationPrincipal Jwt jwt, @RequestBody GenericUserCommand genericUserCommand) {
        LoggedInUser loggedInUser = securityHelper.getJwtUserClaim(jwt, LoggedInUser.class);
        ExecutionUser executionUser = Helpers.buildExecutionUser(loggedInUser);
        return ResponseEntity.ok(new ApiResponse<>(null, userApplicationService.createUser(executionUser,
                genericUserCommand)));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('SU') or @securityHelper.hasAuthorityAndAnyRight(authentication, 'ADMIN', 'USER_UPDATE') or @securityHelper.hasAuthorityAndAnyRight(authentication, 'OWNER', 'USER_UPDATE')")
    ResponseEntity<ApiResponse<UserResponse>> updateUser(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id, @RequestBody GenericUserCommand genericUserCommand) {
        LoggedInUser loggedInUser = securityHelper.getJwtUserClaim(jwt, LoggedInUser.class);
        ExecutionUser executionUser = Helpers.buildExecutionUser(loggedInUser);
        return ResponseEntity.ok(new ApiResponse<>(null, userApplicationService.updateUser(executionUser,
                new UserId(id),
                genericUserCommand)));
    }

    @PatchMapping("/password")
    @PreAuthorize("hasAuthority('SU') or hasAuthority('ADMIN') or hasAuthority('OWNER') or hasAuthority('NORMAL')")
    ResponseEntity<ApiResponse<Void>> updatePassword(@AuthenticationPrincipal Jwt jwt, @RequestBody PasswordModifyUserCommand passwordModifyUserCommand) {
        LoggedInUser loggedInUser = securityHelper.getJwtUserClaim(jwt, LoggedInUser.class);
        ExecutionUser executionUser = Helpers.buildExecutionUser(loggedInUser);
        userApplicationService.updateUserPassword(executionUser, new UserId(loggedInUser.userId()), passwordModifyUserCommand);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SU') or hasAuthority('ADMIN') or hasAuthority('OWNER')")
    ResponseEntity<Void> deleteUser(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        LoggedInUser loggedInUser = securityHelper.getJwtUserClaim(jwt, LoggedInUser.class);
        ExecutionUser executionUser = Helpers.buildExecutionUser(loggedInUser);
        userApplicationService.deleteUser(executionUser, new UserId(id));
        return ResponseEntity.ok().build();
    }

}
