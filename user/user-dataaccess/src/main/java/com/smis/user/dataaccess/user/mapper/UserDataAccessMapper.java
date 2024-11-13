package com.smis.user.dataaccess.user.mapper;

import com.smis.common.core.valueobject.UserId;
import com.smis.common.core.valueobject.UserType;
import com.smis.user.dataaccess.user.entity.UserEntity;
import com.smis.user.domain.entity.RightGroup;
import com.smis.user.domain.entity.User;
import com.smis.user.domain.ports.output.repository.RightGroupRepository;
import com.smis.user.domain.valueobject.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserDataAccessMapper {
    private final RightGroupRepository rightGroupRepository;

    public String userRightGroupsToString(List<RightGroup> rightGroups) {
        return rightGroups == null ||
                rightGroups.isEmpty() ? null
                : rightGroups.stream()
                .map(rightGroup -> rightGroup.getId().getId().toString())
                .collect(Collectors.joining(","));
    }

    public List<RightGroup> stringToRightGroup(String rightGroupIds) {
        if (rightGroupIds == null || rightGroupIds.isBlank()) {
            return List.of();
        }

        return rightGroupRepository
                .findByIds(Arrays.stream(rightGroupIds.split(","))
                        .map(id -> new RightGroupId(UUID.fromString(id)))
                        .toList())
                .orElse(List.of());
    }

    public UserEntity transformUserToEntity(User user) {
        return UserEntity.builder()
                .id(user.getId().getId())
                .userType(user.getUserType().getValue())
                .username(user.getUsername().getValue().toLowerCase())
                .firstname(user.getFirstname().getValue().toLowerCase())
                .otherNames(user.getOtherNames().getValue().toLowerCase())
                .password(user.getPassword() != null ? user.getPassword().getValue() : null)
                .email(user.getEmail().getValue().toLowerCase())
                .phoneNumber(user.getPhoneNumber().getValue())
                .status(user.getUserStatus().getValue())
                .rightGroupIds(userRightGroupsToString(user.getRightGroups()))
                .ownerId(user.getOwnerId() != null ? user.getOwnerId().getId() : null)
                .build();
    }

    public User transformUserEntityToUser(UserEntity userEntity){
        return User.builder()
                .userId(new UserId(userEntity.getId()))
                .userType(new UserType(userEntity.getUserType()))
                .username(new Username(userEntity.getUsername()))
                .firstname(new Firstname(userEntity.getFirstname()))
                .otherNames(new OtherNames(userEntity.getOtherNames()))
                .password(userEntity.getPassword() != null ? new Password(userEntity.getPassword()) : null)
                .email(new Email(userEntity.getEmail()))
                .phoneNumber(new PhoneNumber(userEntity.getPhoneNumber()))
                .userStatus(new UserStatus(userEntity.getStatus()))
                .rightGroups(stringToRightGroup(userEntity.getRightGroupIds()))
                .ownerId(userEntity.getOwnerId() != null ? new OwnerId(userEntity.getOwnerId()) : null)
                .build();
    }

}
