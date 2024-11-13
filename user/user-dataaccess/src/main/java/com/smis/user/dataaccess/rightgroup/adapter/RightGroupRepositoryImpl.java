package com.smis.user.dataaccess.rightgroup.adapter;

import com.smis.common.core.exception.RecordUpdateFailed;
import com.smis.common.core.util.Role;
import com.smis.common.data.util.DataAccessHelper;
import com.smis.user.dataaccess.rightgroup.mapper.RightGroupDataAccessMapper;
import com.smis.user.dataaccess.rightgroup.repository.RightGroupDataRepository;
import com.smis.user.domain.entity.RightGroup;
import com.smis.user.domain.ports.output.repository.RightGroupRepository;
import com.smis.user.domain.valueobject.OwnerId;
import com.smis.user.domain.valueobject.RightGroupId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class RightGroupRepositoryImpl implements RightGroupRepository {
    private final RightGroupDataAccessMapper rightGroupDataAccessMapper;
    private final RightGroupDataRepository rightGroupDataRepository;

    @Override
    public RightGroup create(RightGroup rightGroup) {
        try {
            Instant now = Instant.now();
            rightGroupDataRepository.insertRightGroup(rightGroup.getId().getId(),
                    rightGroup.getName(),
                    rightGroupDataAccessMapper.userRightsToString(rightGroup.getRights()),
                    rightGroup.getCreatorUserId().getId(),
                    rightGroup.getCreatorUserType().getValue(),
                    now,
                    now);
            return rightGroup;
        } catch (Exception e) {
            DataAccessHelper.mapDataAccessErrorToDomainExceptionError(e, "Duplicate right group");
            return null;
        }
    }

    @Override
    public RightGroup update(RightGroup rightGroup) {
        var updatedRecordCount = rightGroupDataRepository.updateRightGroup(rightGroup.getId().getId(),
                rightGroup.getName(),
                rightGroupDataAccessMapper.userRightsToString(rightGroup.getRights()),
                rightGroup.getCreatorUserId().getId(),
                rightGroup.getCreatorUserType().getValue(),
                Instant.now());
        if (updatedRecordCount == 0) {
            String message = "Failed to update right group record";
            log.error(message);
            throw new RecordUpdateFailed(message);
        }
        return rightGroup;
    }

    @Override
    public void delete(RightGroup rightGroup) {
        rightGroupDataRepository.deleteRightGroup(rightGroup.getId().getId(), Instant.now());
    }

    @Override
    public Optional<RightGroup> findById(RightGroupId rightGroupId) {
        return rightGroupDataRepository.findRightGroupById(rightGroupId.getId())
                .map(rightGroupDataAccessMapper::transformEntityToRightGroup);
    }

    @Override
    public Optional<List<RightGroup>> findByIds(List<RightGroupId> rightGroupIds) {
        List<RightGroup> rightGroups = rightGroupDataRepository.findByIds(rightGroupIds.stream()
                        .map(RightGroupId::getId)
                        .collect(Collectors.toList()))
                .stream()
                .map(rightGroupDataAccessMapper::transformEntityToRightGroup)
                .toList();
        return rightGroups.isEmpty() ? Optional.empty() : Optional.of(rightGroups);
    }

    @Override
    public Optional<List<RightGroup>> findAll(int pageNumber, int pageSize) {
        return Optional.ofNullable(rightGroupDataRepository
                        .findAllRightGroups(DataAccessHelper.buildPageable(pageNumber, pageSize)))
                .filter(Page::hasContent)
                .map(page -> page.stream()
                        .map(rightGroupDataAccessMapper::transformEntityToRightGroup)
                        .toList());
    }

    @Override
    public long countAll() {
        return rightGroupDataRepository.countAllRightGroups();
    }

    @Override
    public Optional<List<RightGroup>> findAll(OwnerId ownerId, int pageNumber, int pageSize) {
        return Optional.ofNullable(rightGroupDataRepository
                        .findAllRightGroupsByUserIdAndUserType(Role.OWNER, ownerId.getId(),
                                DataAccessHelper.buildPageable(pageNumber, pageSize)))
                .filter(Page::hasContent)
                .map(page -> page.stream()
                        .map(rightGroupDataAccessMapper::transformEntityToRightGroup)
                        .toList());
    }

    @Override
    public long countAll(OwnerId ownerId) {
        return rightGroupDataRepository.countAllRightGroupsByUserIdAndUserType(Role.OWNER, ownerId.getId());
    }
}
