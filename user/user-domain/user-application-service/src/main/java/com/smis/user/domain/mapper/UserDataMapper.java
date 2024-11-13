package com.smis.user.domain.mapper;

import com.smis.common.core.valueobject.UserId;
import com.smis.common.core.valueobject.UserType;
import com.smis.user.domain.dto.user.GenericUserCommand;
import com.smis.user.domain.dto.user.UserResponse;
import com.smis.user.domain.entity.RightGroup;
import com.smis.user.domain.entity.User;
import com.smis.user.domain.valueobject.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class UserDataMapper {
    private final RightGroupDataMapper rightGroupDataMapper;

    public User transformGenericUserCommandToUser(GenericUserCommand genericUserCommand,
                                                  UserId userId,
                                                  List<RightGroup> rightGroups,
                                                  Password password,
                                                  OwnerId ownerId) {
        return transformGenericUserCommandToUser(null,
                genericUserCommand,
                userId,
                rightGroups,
                password,
                ownerId);
    }

    public User transformGenericUserCommandToUser(User existingUser,
                                                  GenericUserCommand genericUserCommand,
                                                  List<RightGroup> rightGroups,
                                                  Password password) {

        return transformGenericUserCommandToUser(existingUser,
                genericUserCommand,
                null,
                rightGroups,
                password,
                null);
    }

    private User transformGenericUserCommandToUser(User existingUser,
                                                   GenericUserCommand genericUserCommand,
                                                   UserId userId,
                                                   List<RightGroup> rightGroups,
                                                   Password password,
                                                   OwnerId ownerId) {
        User.Builder user = User.builder();
        if (existingUser != null) {
            user.userId(existingUser.getId());

        } else if (userId != null) {
            user.userId(userId);
        }

        if (password != null) {
            user.password(password);
        }

        if (existingUser != null) {
            user.userType(existingUser.getUserType());
        } else {
            user.userType(new UserType(genericUserCommand.userType()));
        }
        user.username(new Username(genericUserCommand.username()));
        user.firstname(new Firstname(genericUserCommand.firstName()));
        user.otherNames(new OtherNames(genericUserCommand.otherNames()));
        user.email(new Email(genericUserCommand.email()));
        user.phoneNumber(new PhoneNumber(genericUserCommand.phoneNumber()));
        user.userStatus(new UserStatus(genericUserCommand.status()));

        if (existingUser != null) {
            user.ownerId(existingUser.getOwnerId());
        } else if (ownerId != null) {
            user.ownerId(ownerId);
        }
        user.rightGroups(rightGroups);
        return user.build();
    }

    public UserResponse transformUserToUserResponse(User user) {
        return transformUserToUserResponse(user, false);
    }

    public UserResponse transformUserToUserResponse(User user, boolean withPassword) {
        UUID ownerId = null;
        if (user.getOwnerId() != null) {
            ownerId = user.getOwnerId().getId();
        }

        return new UserResponse(user.getId().getId(),
                user.getUserType().getValue(),
                user.getUsername().getValue(),
                withPassword && user.getPassword() != null ? user.getPassword().getValue() : null,
                user.getFirstname().getValue(),
                user.getOtherNames().getValue(),
                user.getEmail().getValue(),
                user.getPhoneNumber().getValue(),
                user.getUserStatus().getValue(),
                ownerId,
                user.getRightGroups().stream()
                        .map(rightGroupDataMapper::transformRightGroupToRightGroupResponse)
                        .toList());
    }
}
