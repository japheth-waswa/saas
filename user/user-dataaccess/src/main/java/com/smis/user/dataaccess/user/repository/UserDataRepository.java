package com.smis.user.dataaccess.user.repository;

import com.smis.common.core.util.Role;
import com.smis.user.dataaccess.user.entity.UserEntity;
import com.smis.user.domain.util.Status;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface UserDataRepository extends JpaRepository<UserEntity, UUID> {

    @Modifying
    @Transactional
    @Query("INSERT INTO UserEntity (id, userType, username, firstname, otherNames, " +
            "password, email, phoneNumber, status, rightGroupIds, ownerId, createdAt, updatedAt) " +
            "VALUES (:id, :userType, :username, :firstname, :otherNames, " +
            ":password, :email, :phoneNumber, :status, :rightGroupIds, :ownerId, :createdAt, :updatedAt)")
    void insertUser(@NotNull UUID id,
                    @NotNull Role userType,
                    @NotNull String username,
                    @NotNull String firstname,
                    @NotNull String otherNames,
                    @NotNull String password,
                    @NotNull String email,
                    long phoneNumber,
                    @NotNull Status status,
                    @NotNull String rightGroupIds,
                    @NotNull UUID ownerId,
                    @NotNull Instant createdAt,
                    @NotNull Instant updatedAt);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.userType = :userType, u.username = :username, u.firstname = :firstname, u.otherNames = :otherNames, " +
            "u.email = :email, u.phoneNumber = :phoneNumber, u.status = :status, u.rightGroupIds = :rightGroupIds, " +
            "u.ownerId = :ownerId, u.updatedAt = :updatedAt WHERE u.id = :id")
    int updateUser(@NotNull UUID id,
                   @NotNull Role userType,
                   @NotNull String username,
                   @NotNull String firstname,
                   @NotNull String otherNames,
                   @NotNull String email,
                   long phoneNumber,
                   @NotNull Status status,
                   @NotNull String rightGroupIds,
                   @NotNull UUID ownerId,
                   @NotNull Instant updatedAt
    );

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.password = :password, u.updatedAt = :updatedAt WHERE u.id = :id")
    int updateUserPassword(@NotNull UUID id, @NotNull String password, @NotNull Instant updatedAt);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.deletedAt = :deletedAt WHERE u.id = :id")
    void deleteUserPassword(@NotNull UUID id, @NotNull Instant deletedAt);

    @Query("SELECT u FROM UserEntity u WHERE u.deletedAt IS NULL AND u.id = :id")
    Optional<UserEntity> findUserById(@NotNull UUID id);

    @Query("SELECT u FROM UserEntity u WHERE u.deletedAt IS NULL AND u.username = :username")
    Optional<UserEntity> findUserByUsername(@NotNull String username);

    @Query("SELECT u FROM UserEntity u WHERE u.deletedAt IS NULL")
    Page<UserEntity> findAllUsers(@NotNull Pageable pageable);

    @Query("SELECT count(u) FROM UserEntity u WHERE u.deletedAt IS NULL")
    long countAllUsers();

    @Query("SELECT u FROM UserEntity u WHERE u.deletedAt IS NULL AND u.userType = :userType")
    Page<UserEntity> findAllUsers(@NotNull Role userType, @NotNull Pageable pageable);

    @Query("SELECT count(u) FROM UserEntity u WHERE u.deletedAt IS NULL AND u.userType = :userType")
    long countAllUsers(@NotNull Role userType);

    @Query("SELECT u FROM UserEntity u WHERE u.deletedAt IS NULL AND u.ownerId = :ownerId")
    Page<UserEntity> findAllUsers(@NotNull UUID ownerId, @NotNull Pageable pageable);

    @Query("SELECT count(u) FROM UserEntity u WHERE u.deletedAt IS NULL AND u.ownerId = :ownerId")
    long countAllUsers(@NotNull UUID ownerId);

    @Query("SELECT u FROM UserEntity u WHERE u.deletedAt IS NULL AND (u.firstname LIKE %:searchTerm% OR u.otherNames LIKE %:searchTerm% OR u.email LIKE %:searchTerm%)")
    Page<UserEntity> findAllUsers(@NotNull String searchTerm, @NotNull Pageable pageable);

    @Query("SELECT count(u) FROM UserEntity u WHERE u.deletedAt IS NULL AND (u.firstname LIKE %:searchTerm% OR u.otherNames LIKE %:searchTerm% OR u.email LIKE %:searchTerm%)")
    long countAllUsers(@NotNull String searchTerm);

    @Query("SELECT u FROM UserEntity u WHERE u.deletedAt IS NULL AND u.ownerId = :ownerId AND (u.firstname LIKE %:searchTerm% OR u.otherNames LIKE %:searchTerm% OR u.email LIKE %:searchTerm%)")
    Page<UserEntity> findAllUsers(@NotNull UUID ownerId, @NotNull String searchTerm, @NotNull Pageable pageable);

    @Query("SELECT count(u) FROM UserEntity u WHERE u.deletedAt IS NULL AND u.ownerId = :ownerId AND (u.firstname LIKE %:searchTerm% OR u.otherNames LIKE %:searchTerm% OR u.email LIKE %:searchTerm%)")
    long countAllUsers(@NotNull UUID ownerId, @NotNull String searchTerm);

}
