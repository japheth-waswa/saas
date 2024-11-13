package com.smis.user.dataaccess.rightgroup.repository;

import com.smis.common.core.util.Role;
import com.smis.user.dataaccess.rightgroup.entity.RightGroupEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RightGroupDataRepository extends JpaRepository<RightGroupEntity, UUID> {

    @Modifying
    @Transactional
    @Query("INSERT INTO RightGroupEntity (id, name, rights, creatorUserId, creatorUserType, createdAt, updatedAt) " +
            "VALUES (:id, :name, :rights, :creatorUserId, :creatorUserType, :createdAt, :updatedAt)")
    void insertRightGroup(@NotNull UUID id,
                          @NotNull String name,
                          @NotNull String rights,
                          @NotNull UUID creatorUserId,
                          @NotNull Role creatorUserType,
                          @NotNull Instant createdAt,
                          @NotNull Instant updatedAt);

    @Modifying
    @Transactional
    @Query("UPDATE RightGroupEntity r SET r.name = :name, r.rights = :rights, r.creatorUserId = :creatorUserId, " +
            "r.creatorUserType = :creatorUserType, r.updatedAt = :updatedAt WHERE r.id = :id")
    int updateRightGroup(@NotNull UUID id,
                          @NotNull String name,
                          String rights,
                          @NotNull UUID creatorUserId,
                          @NotNull Role creatorUserType,
                          @NotNull Instant updatedAt
    );

    @Modifying
    @Transactional
    @Query("UPDATE RightGroupEntity r SET r.deletedAt = :deletedAt WHERE r.id = :id")
    void deleteRightGroup(@NotNull UUID id, @NotNull Instant deletedAt);

    @Query("SELECT r FROM RightGroupEntity r WHERE r.deletedAt IS NULL AND r.id = :id")
    Optional<RightGroupEntity> findRightGroupById(@NotNull UUID id);

    @Query("SELECT r FROM RightGroupEntity r WHERE r.deletedAt IS NULL AND r.id IN :ids")
    List<RightGroupEntity> findByIds(@NotNull List<UUID> ids);

    @Query("SELECT r FROM RightGroupEntity r WHERE r.deletedAt IS NULL")
    Page<RightGroupEntity> findAllRightGroups(@NotNull Pageable pageable);

    @Query("SELECT count(r) FROM RightGroupEntity r WHERE r.deletedAt IS NULL")
    long countAllRightGroups();

    @Query("SELECT r FROM RightGroupEntity r WHERE r.deletedAt IS NULL AND r.creatorUserId = :userId AND  r.creatorUserType = :userType")
    Page<RightGroupEntity> findAllRightGroupsByUserIdAndUserType(@NotNull Role userType, @NotNull UUID userId, @NotNull Pageable pageable);

    @Query("SELECT count(r) FROM RightGroupEntity r WHERE r.deletedAt IS NULL AND r.creatorUserId = :userId AND  r.creatorUserType = :userType")
    long countAllRightGroupsByUserIdAndUserType(@NotNull Role userType, @NotNull UUID userId);

}
