package com.smis.user.domain.ports.input.service;

import com.smis.common.core.valueobject.ExecutionUser;
import com.smis.user.domain.dto.rightgroup.GenericRightGroupCommand;
import com.smis.user.domain.dto.rightgroup.RightGroupListResponse;
import com.smis.user.domain.dto.rightgroup.RightGroupResponse;
import com.smis.user.domain.valueobject.RightGroupId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public interface RightGroupApplicationService {
    RightGroupResponse createRightGroup(@NotNull ExecutionUser executionUser, @NotNull @Valid GenericRightGroupCommand genericRightGroupCommand);

    RightGroupResponse updateRightGroup(@NotNull ExecutionUser executionUser, @NotNull RightGroupId rightGroupId, @NotNull @Valid GenericRightGroupCommand genericRightGroupCommand);

    void deleteRightGroup(@NotNull ExecutionUser executionUser, @NotNull RightGroupId rightGroupId);

    RightGroupListResponse findAllRightGroups(@NotNull ExecutionUser executionUser, @Min(0) int pageNumber, @Min(1) int pageSize);

}
