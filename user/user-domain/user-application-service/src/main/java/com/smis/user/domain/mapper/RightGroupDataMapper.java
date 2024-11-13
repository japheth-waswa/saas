package com.smis.user.domain.mapper;

import com.smis.common.core.valueobject.ExecutionUser;
import com.smis.common.core.valueobject.UserRight;
import com.smis.user.domain.dto.rightgroup.GenericRightGroupCommand;
import com.smis.user.domain.dto.rightgroup.RightGroupResponse;
import com.smis.user.domain.entity.RightGroup;
import com.smis.user.domain.valueobject.RightGroupId;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class RightGroupDataMapper {

    public RightGroup transformGenericRightGroupCommandToRightGroup(ExecutionUser executionUser, GenericRightGroupCommand genericRightGroupCommand, RightGroupId rightGroupId) {
        RightGroup.Builder rightGroup = RightGroup.builder()
                .creatorUserId(executionUser.getUserId())
                .creatorUserType(executionUser.getUserType());
        if (rightGroupId != null) {
            rightGroup.rightGroupId(rightGroupId);
        }
        rightGroup.name(genericRightGroupCommand.name());
        rightGroup.rights(genericRightGroupCommand.rights().stream().map(UserRight::new).collect(Collectors.toList()));
        return rightGroup.build();
    }

    public RightGroup transformGenericRightGroupCommandToRightGroup(RightGroup existingRightGroup, GenericRightGroupCommand genericRightGroupCommand) {
        RightGroup.Builder rightGroup = RightGroup.builder()
                .rightGroupId(existingRightGroup.getId())
                .creatorUserId(existingRightGroup.getCreatorUserId())
                .creatorUserType(existingRightGroup.getCreatorUserType());
        rightGroup.name(genericRightGroupCommand.name());
        rightGroup.rights(genericRightGroupCommand.rights().stream().map(UserRight::new).collect(Collectors.toList()));
        return rightGroup.build();
    }

    public RightGroupResponse transformRightGroupToRightGroupResponse(RightGroup rightGroup) {
        return new RightGroupResponse(rightGroup.getId().getId(),
                rightGroup.getName(),
                rightGroup.getRights().stream().map(UserRight::getValue).toList());
    }
}
