package com.smis.user.domain.command;

import com.smis.common.core.dto.LoggedInUser;
import com.smis.common.core.exception.AccessDenied;
import com.smis.common.core.exception.InvalidLoginCredentials;
import com.smis.common.core.exception.RecordNotFound;
import com.smis.common.core.util.Role;
import com.smis.common.core.valueobject.ExecutionUser;
import com.smis.common.core.valueobject.UserId;
import com.smis.common.core.valueobject.UserType;
import com.smis.security.util.JwtUtil;
import com.smis.security.util.SecurityHelper;
import com.smis.user.domain.UserDomainService;
import com.smis.user.domain.dto.user.*;
import com.smis.user.domain.entity.RightGroup;
import com.smis.user.domain.entity.User;
import com.smis.user.domain.event.UserCreatedEvent;
import com.smis.user.domain.event.UserDeletedEvent;
import com.smis.user.domain.event.UserUpdatedEvent;
import com.smis.user.domain.mapper.UserDataMapper;
import com.smis.user.domain.ports.output.repository.RightGroupRepository;
import com.smis.user.domain.ports.output.repository.UserRepository;
import com.smis.user.domain.util.UserDomainConstants;
import com.smis.user.domain.valueobject.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.smis.common.core.util.Helpers.INVALID_LOGIN_CREDENTIALS_MESSAGE;
import static com.smis.common.core.util.Helpers.SESSION_EXPIRY;

@Slf4j
@Component
@AllArgsConstructor
public class UserCommandHandler {
    private final RightGroupRepository rightGroupRepository;
    private final UserRepository userRepository;
    private final UserDataMapper userDataMapper;
    private final UserDomainService userDomainService;
    private final SecurityHelper securityHelper;
    private final JwtUtil jwtUtil;

    private List<RightGroup> fetchRightGroups(GenericUserCommand genericUserCommand) {
        return rightGroupRepository.findByIds(genericUserCommand.rightGroupIds().stream()
                .map(RightGroupId::new)
                .toList()).orElseThrow(() -> {
            log.error("Right groups not found for ids: {}", genericUserCommand.rightGroupIds());
            return new RecordNotFound("Right groups not found");
        });
    }

    public LoginResponse login(LoginPayload loginPayload) {
        try {
            log.info("Logging in user with username: {}", loginPayload.username());
            User user = userRepository.findByUsername(new Username(loginPayload.username()))
                    .orElseThrow(() -> {
                        log.error("User with username: {} not found in our records", loginPayload.username());
                        return new RecordNotFound("User not found");
                    });

            if (!securityHelper.passwordMatches(loginPayload.password(), user.getPassword().getValue())) {
                throw new InvalidLoginCredentials(INVALID_LOGIN_CREDENTIALS_MESSAGE);
            }

            if (!user.isAllowedToLogin()) {
                log.error("User with username: {} is not allowed to login", loginPayload.username());
                throw new AccessDenied("User not allowed to login");
            }

            Set<String> authorities = new HashSet<>();
            authorities.add(user.getUserType().getValue().toString());
            if (user.getRightGroups() != null && !user.getRightGroups().isEmpty()) {
                user.getRightGroups().forEach(rightGroup -> {
                    if (rightGroup.getRights() != null && !rightGroup.getRights().isEmpty()) {
                        authorities.addAll(rightGroup.getRights().stream()
                                .map(userRight -> userRight.getValue().toString())
                                .collect(Collectors.toSet()));
                    }
                });
            }

            //todo include message publishing that you have logged in

            return new LoginResponse(jwtUtil.generateJwt(SESSION_EXPIRY, user.getUsername().getValue(),
                    new ArrayList<>(authorities),
                    new LoggedInUser(user.getId().getId(), user.getUserType().getValue(), user.getFirstname().getValue() + " " + user.getOtherNames().getValue()),
                    null), SESSION_EXPIRY);
        } catch (Exception e) {
            log.error("Failed to login", e);
            throw new InvalidLoginCredentials(INVALID_LOGIN_CREDENTIALS_MESSAGE);
        }
    }

    @Transactional
    public UserResponse createUser(ExecutionUser executionUser, GenericUserCommand genericUserCommand) {
        String password = UserDomainConstants.randomPasswordGenerator(8);
        OwnerId ownerId = executionUser.getUserType().getValue().equals(Role.OWNER) ? new OwnerId(executionUser.getUserId().getId()) : null;
        User user = userDataMapper.transformGenericUserCommandToUser(genericUserCommand,
                new UserId(UUID.randomUUID()),
                fetchRightGroups(genericUserCommand),
                new Password(password), ownerId);

        UserCreatedEvent userCreatedEvent = userDomainService.createUser(user, executionUser);
        user.setPassword(new Password(securityHelper.encodePassword(user.getPassword().getValue())));
        User savedUser = userRepository.save(user);
        //todo include message publishing with auto-generated password ie `password`
        return userDataMapper.transformUserToUserResponse(savedUser);
    }

    @Transactional
    public UserResponse updateUser(ExecutionUser executionUser, UserId userId, GenericUserCommand genericUserCommand) {

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RecordNotFound("User not found"));

        User user = userDataMapper.transformGenericUserCommandToUser(existingUser,
                genericUserCommand,
                fetchRightGroups(genericUserCommand),
                null);
        UserUpdatedEvent userUpdatedEvent = userDomainService.updateUser(user, executionUser);
        User savedUser = userRepository.update(user);
        //todo include message publishing
        return userDataMapper.transformUserToUserResponse(savedUser);
    }

    @Transactional
    public UserResponse updateUserPassword(ExecutionUser executionUser, UserId userId, PasswordModifyUserCommand passwordModifyUserCommand) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RecordNotFound("User not found"));
        UserUpdatedEvent userUpdatedEvent = userDomainService.updateUserPassword(user, executionUser);
        user.setPassword(new Password(securityHelper.encodePassword(user.getPassword().getValue())));
        User savedUser = userRepository.updatePassword(user);
        //todo include message publishing
        return userDataMapper.transformUserToUserResponse(savedUser);
    }

    @Transactional
    public void deleteUser(ExecutionUser executionUser, UserId userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RecordNotFound("User not found"));
        UserDeletedEvent userDeletedEvent = userDomainService.deleteUser(user, executionUser);
        userRepository.delete(user);
        //todo include message publishing
    }

    public UserResponse findUser(ExecutionUser executionUser, Username username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User with username: {} not found", username.getValue());
                    return new RecordNotFound("User not found");
                });
        return userDataMapper.transformUserToUserResponse(user, true);
    }

    public UserListResponse findAllUsers(ExecutionUser executionUser, int pageNumber, int pageSize) {
        checkAllowedToFetchUsers(executionUser, null);
        List<User> users = userRepository.findAll(pageNumber, pageSize)
                .orElseThrow(() -> {
                    log.error("Users not found in pageNumber: {} and pageSize:{}", pageNumber, pageSize);
                    return new RecordNotFound("Users not found");
                });
        return new UserListResponse(userRepository.countAll(),
                users.stream()
                        .map(userDataMapper::transformUserToUserResponse)
                        .toList());
    }

    public UserListResponse findAllUsersByUserType(ExecutionUser executionUser, UserType userType, int pageNumber, int pageSize) {
        checkAllowedToFetchUsers(executionUser, userType);
        List<User> users = userRepository.findAllByUserType(userType, pageNumber, pageSize)
                .orElseThrow(() -> {
                    log.error("Users not found with userType:{} in pageNumber: {} and pageSize:{}", userType.getValue(), pageNumber, pageSize);
                    return new RecordNotFound("Users not found");
                });
        return new UserListResponse(userRepository.countAllByUserType(userType),
                users.stream()
                        .map(userDataMapper::transformUserToUserResponse)
                        .toList());
    }

    public UserListResponse findAllUsersByNames(ExecutionUser executionUser, Names names, int pageNumber, int pageSize) {
        checkAllowedToFetchUsers(executionUser, null);
        List<User> users = userRepository.findAllBySearchTerm(names.getValue(), pageNumber, pageSize)
                .orElseThrow(() -> {
                    log.error("Users not found with names:{} in pageNumber: {} and pageSize:{}", names.getValue(), pageNumber, pageSize);
                    return new RecordNotFound("Users not found");
                });
        return new UserListResponse(userRepository.countAllBySearchTerm(names.getValue()),
                users.stream()
                        .map(userDataMapper::transformUserToUserResponse)
                        .toList());
    }

    public UserListResponse findAllUsersByOwnerId(ExecutionUser executionUser, OwnerId ownerId, int pageNumber, int pageSize) {
        checkAllowedToFetchUsers(executionUser, new UserType(Role.NORMAL));
        List<User> users = userRepository.findAllByOwnerId(ownerId, pageNumber, pageSize)
                .orElseThrow(() -> {
                    log.error("Users not found with  pageNumber: {} and pageSize:{} and ownerId: {}", pageNumber, pageSize, ownerId.getId());
                    return new RecordNotFound("Users not found");
                });
        return new UserListResponse(userRepository.countAllByOwnerId(ownerId),
                users.stream()
                        .map(userDataMapper::transformUserToUserResponse)
                        .toList());
    }

    public UserListResponse findAllUsersByOwnerIdAndNames(ExecutionUser executionUser, OwnerId ownerId, Names names, int pageNumber, int pageSize) {
        checkAllowedToFetchUsers(executionUser, new UserType(Role.NORMAL));
        List<User> users = userRepository.findAllByOwnerIdAndSearchTerm(ownerId, names.getValue(), pageNumber, pageSize)
                .orElseThrow(() -> {
                    log.error("Users not found with names:{} in pageNumber: {} and pageSize:{} and ownerId: {}", names.getValue(), pageNumber, pageSize, ownerId.getId());
                    return new RecordNotFound("Users not found");
                });
        return new UserListResponse(userRepository.countAllByOwnerIdAndSearchTerm(ownerId, names.getValue()),
                users.stream()
                        .map(userDataMapper::transformUserToUserResponse)
                        .toList());
    }


    private void checkAllowedToFetchUsers(ExecutionUser executionUser, UserType userType) {
        //only SU can fetch all users
        if (executionUser.getUserType().getValue().equals(Role.SU)) {
            log.info("Super User can fetch all users");
            return;
        }

        if (userType == null && !executionUser.getUserType().getValue().equals(Role.SU)) {
            log.error("Only Super User can fetch all users");
            throw new AccessDenied("Access Denied, only super user can fetch all users");
        }


        //only SU can fetch SU & ADMIN users
        if ((userType != null && userType.getValue().equals(Role.SU)) ||
                (userType != null && userType.getValue().equals(Role.ADMIN) && !executionUser.getUserType().getValue().equals(Role.SU))) {
            log.error("Only Super User can fetch users with userType: {}", userType.getValue());
            throw new AccessDenied("Access Denied, only Super User can fetch users with userType: " + userType.getValue());
        }

        //only ADMIN can fetch OWNER users
        if (userType != null && userType.getValue().equals(Role.OWNER) && !executionUser.getUserType().getValue().equals(Role.ADMIN)) {
            log.error("Only Admin can fetch users with userType: {}", userType.getValue());
            throw new AccessDenied("Access Denied, only Admin can fetch users with userType: " + userType.getValue());
        }

        //only OWNER can fetch NORMAL users
        if (userType != null && userType.getValue().equals(Role.NORMAL) && !executionUser.getUserType().getValue().equals(Role.OWNER)) {
            log.error("Only Owner can fetch users with userType: {}", userType.getValue());
            throw new AccessDenied("Access Denied, only Owner can fetch users with userType: " + userType.getValue());
        }
    }
}
