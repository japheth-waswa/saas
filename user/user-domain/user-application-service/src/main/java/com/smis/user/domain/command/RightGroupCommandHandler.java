package com.smis.user.domain.command;

import com.smis.common.core.exception.RecordNotFound;
import com.smis.common.core.registry.OwnerManagedRightsRegistry;
import com.smis.common.core.util.Helpers;
import com.smis.common.core.valueobject.ExecutionUser;
import com.smis.user.domain.UserDomainService;
import com.smis.user.domain.dto.rightgroup.GenericRightGroupCommand;
import com.smis.user.domain.dto.rightgroup.RightGroupListResponse;
import com.smis.user.domain.dto.rightgroup.RightGroupResponse;
import com.smis.user.domain.entity.RightGroup;
import com.smis.user.domain.event.RightGroupCreatedEvent;
import com.smis.user.domain.event.RightGroupDeletedEvent;
import com.smis.user.domain.event.RightGroupUpdatedEvent;
import com.smis.user.domain.mapper.RightGroupDataMapper;
import com.smis.user.domain.ports.output.repository.RightGroupRepository;
import com.smis.user.domain.valueobject.OwnerId;
import com.smis.user.domain.valueobject.RightGroupId;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class RightGroupCommandHandler {
    private final UserDomainService userDomainService;
    private final RightGroupDataMapper rightGroupDataMapper;
    private final RightGroupRepository rightGroupRepository;
    private final OwnerManagedRightsRegistry ownerManagedRightsRegistry;

    @Transactional
    public RightGroupResponse createRightGroup(ExecutionUser executionUser, GenericRightGroupCommand genericRightGroupCommand) {
        RightGroup rightGroup = rightGroupDataMapper.transformGenericRightGroupCommandToRightGroup(executionUser, genericRightGroupCommand, new RightGroupId(UUID.randomUUID()));
        RightGroupCreatedEvent rightGroupCreatedEvent = userDomainService.createRightGroup(rightGroup,
                executionUser,
                ownerManagedRightsRegistry.getOwnerManagedRights());
        RightGroup savedRightGroup = rightGroupRepository.create(rightGroup);
        log.warn("payload: {}", savedRightGroup);
        //todo include message publishing
        return rightGroupDataMapper.transformRightGroupToRightGroupResponse(savedRightGroup);
    }

    @Transactional
    public RightGroupResponse updateRightGroup(ExecutionUser executionUser, RightGroupId rightGroupId, GenericRightGroupCommand genericRightGroupCommand) {
        RightGroup rightGroupRecord = rightGroupRepository.findById(rightGroupId)
                .orElseThrow(() -> {
                    log.error("RightGroup with id: {} not found for update", rightGroupId.getId());
                    return new RecordNotFound("Right group not found");
                });

        RightGroup rightGroup = rightGroupDataMapper.transformGenericRightGroupCommandToRightGroup(
                rightGroupRecord,
                genericRightGroupCommand);
        RightGroupUpdatedEvent rightGroupUpdatedEvent = userDomainService.updateRightGroup(rightGroup,
                executionUser,
                ownerManagedRightsRegistry.getOwnerManagedRights());
        RightGroup savedRightGroup = rightGroupRepository.update(rightGroup);
        //todo include message publishing
        return rightGroupDataMapper.transformRightGroupToRightGroupResponse(savedRightGroup);
    }

    @Transactional
    public void deleteRightGroup(ExecutionUser executionUser, RightGroupId rightGroupId) {
        RightGroup rightGroup = rightGroupRepository.findById(rightGroupId)
                .orElseThrow(() -> {
                    log.error("RightGroup with id: {} not found", rightGroupId.getId());
                    return new RecordNotFound("Right group not found");
                });
        RightGroupDeletedEvent rightGroupDeletedEvent = userDomainService.deleteRightGroup(rightGroup,
                executionUser,
                ownerManagedRightsRegistry.getOwnerManagedRights());
        rightGroupRepository.delete(rightGroup);
        //todo include message publishing
    }

    public RightGroupListResponse findAllRightGroups(ExecutionUser executionUser, int pageNumber, int pageSize) {
        Optional<List<RightGroup>> rightGroupsOptional;
        var isOwner = Helpers.isOwner(executionUser);
        if (isOwner) {
            rightGroupsOptional = rightGroupRepository.findAll(new OwnerId(executionUser.getUserId().getId()), pageNumber, pageSize);
        } else {
            rightGroupsOptional = rightGroupRepository.findAll(pageNumber, pageSize);
        }

        if (rightGroupsOptional.isEmpty()) {
            log.error("No right groups found");
            throw new RecordNotFound("No right groups found");
        }

        return new RightGroupListResponse(isOwner ? rightGroupRepository.countAll(new OwnerId(executionUser.getUserId().getId()))
                : rightGroupRepository.countAll(),
                rightGroupsOptional.get().stream()
                        .map(rightGroupDataMapper::transformRightGroupToRightGroupResponse)
                        .toList());
    }
}
