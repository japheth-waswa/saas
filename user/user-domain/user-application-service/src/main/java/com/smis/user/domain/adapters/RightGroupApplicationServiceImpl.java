package com.smis.user.domain.adapters;

import com.smis.common.core.valueobject.ExecutionUser;
import com.smis.user.domain.command.RightGroupCommandHandler;
import com.smis.user.domain.dto.rightgroup.GenericRightGroupCommand;
import com.smis.user.domain.dto.rightgroup.RightGroupListResponse;
import com.smis.user.domain.dto.rightgroup.RightGroupResponse;
import com.smis.user.domain.ports.input.service.RightGroupApplicationService;
import com.smis.user.domain.valueobject.RightGroupId;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@AllArgsConstructor
public class RightGroupApplicationServiceImpl implements RightGroupApplicationService {
    private final RightGroupCommandHandler rightGroupCommandHandler;

    @Override
    public RightGroupResponse createRightGroup(ExecutionUser executionUser, GenericRightGroupCommand genericRightGroupCommand) {
        return rightGroupCommandHandler.createRightGroup(executionUser, genericRightGroupCommand);
    }

    @Override
    public RightGroupResponse updateRightGroup(ExecutionUser executionUser, RightGroupId rightGroupId, GenericRightGroupCommand genericRightGroupCommand) {
        return rightGroupCommandHandler.updateRightGroup(executionUser, rightGroupId, genericRightGroupCommand);
    }

    @Override
    public void deleteRightGroup(ExecutionUser executionUser, RightGroupId rightGroupId) {
        rightGroupCommandHandler.deleteRightGroup(executionUser, rightGroupId);
    }

    @Override
    public RightGroupListResponse findAllRightGroups(ExecutionUser executionUser, int pageNumber, int pageSize) {
        return rightGroupCommandHandler.findAllRightGroups( executionUser, pageNumber,  pageSize);
    }
}
