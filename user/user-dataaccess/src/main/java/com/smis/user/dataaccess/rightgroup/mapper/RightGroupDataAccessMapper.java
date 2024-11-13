package com.smis.user.dataaccess.rightgroup.mapper;

import com.smis.common.core.util.Right;
import com.smis.common.core.valueobject.UserId;
import com.smis.common.core.valueobject.UserRight;
import com.smis.common.core.valueobject.UserType;
import com.smis.user.dataaccess.rightgroup.entity.RightGroupEntity;
import com.smis.user.domain.entity.RightGroup;
import com.smis.user.domain.valueobject.RightGroupId;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RightGroupDataAccessMapper {

    public String userRightsToString(List<UserRight> userRights) {
        return userRights == null || userRights.isEmpty() ? null :
                userRights.stream()
                        .map(userRight -> userRight.getValue().name())
                        .collect(Collectors.joining(","));
    }

    public List<UserRight> stringToUserRights(String rights) {
        return rights == null || rights.isBlank() ? List.of() :
                Arrays.stream(rights.split(","))
                        .map(right -> new UserRight(Right.valueOf(right)))
                        .collect(Collectors.toList());
    }

    public RightGroupEntity transformRightGroupToEntity(RightGroup rightGroup) {
        return RightGroupEntity.builder()
                .id(rightGroup.getId().getId())
                .name(rightGroup.getName())
                .rights(userRightsToString(rightGroup.getRights()))
                .creatorUserId(rightGroup.getCreatorUserId().getId())
                .creatorUserType(rightGroup.getCreatorUserType().getValue())
                .build();
    }

    public RightGroup transformEntityToRightGroup(RightGroupEntity rightGroupEntity) {
        return RightGroup.builder()
                .rightGroupId(new RightGroupId(rightGroupEntity.getId()))
                .name(rightGroupEntity.getName())
                .rights(stringToUserRights(rightGroupEntity.getRights()))
                .creatorUserId(new UserId(rightGroupEntity.getCreatorUserId()))
                .creatorUserType(new UserType(rightGroupEntity.getCreatorUserType()))
                .build();
    }

}
