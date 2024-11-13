package com.smis.user.domain.ports.output.repository;

import com.smis.user.domain.entity.RightGroup;
import com.smis.user.domain.valueobject.OwnerId;
import com.smis.user.domain.valueobject.RightGroupId;

import java.util.List;
import java.util.Optional;

public interface RightGroupRepository {
    RightGroup create(RightGroup rightGroup);

    RightGroup update(RightGroup rightGroup);

    void delete(RightGroup rightGroup);

    Optional<RightGroup> findById(RightGroupId rightGroupId);

    Optional<List<RightGroup>> findByIds(List<RightGroupId> rightGroupIds);

    Optional<List<RightGroup>> findAll(int pageNumber, int pageSize);

    long countAll();

    Optional<List<RightGroup>> findAll(OwnerId ownerId, int pageNumber, int pageSize);

    long countAll(OwnerId ownerId);

}
