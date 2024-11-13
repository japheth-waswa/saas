package com.smis.user.dataaccess.user.adapter;

import com.smis.common.core.exception.RecordUpdateFailed;
import com.smis.common.core.valueobject.UserId;
import com.smis.common.core.valueobject.UserType;
import com.smis.common.data.util.DataAccessHelper;
import com.smis.user.dataaccess.user.mapper.UserDataAccessMapper;
import com.smis.user.dataaccess.user.repository.UserDataRepository;
import com.smis.user.domain.entity.User;
import com.smis.user.domain.ports.output.repository.UserRepository;
import com.smis.user.domain.valueobject.OwnerId;
import com.smis.user.domain.valueobject.Username;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserDataRepository userDataRepository;
    private final UserDataAccessMapper userDataAccessMapper;

    @Override
    public User save(User user) {
        var now = Instant.now();
        var userEntity = userDataAccessMapper.transformUserToEntity(user);
        try {
            userDataRepository.insertUser(userEntity.getId(),
                    userEntity.getUserType(),
                    userEntity.getUsername(),
                    userEntity.getFirstname(),
                    userEntity.getOtherNames(),
                    userEntity.getPassword(),
                    userEntity.getEmail(),
                    userEntity.getPhoneNumber(),
                    userEntity.getStatus(),
                    userEntity.getRightGroupIds(),
                    userEntity.getOwnerId(),
                    now,
                    now);
        } catch (Exception e) {
            DataAccessHelper.mapDataAccessErrorToDomainExceptionError(e, "Duplicate user");
            return null;
        }
        return user;
    }

    @Override
    public User update(User user) {
        var userEntity = userDataAccessMapper.transformUserToEntity(user);
        var updatedRecordCount = userDataRepository.updateUser(userEntity.getId(),
                userEntity.getUserType(),
                userEntity.getUsername(),
                userEntity.getFirstname(),
                userEntity.getOtherNames(),
                userEntity.getEmail(),
                userEntity.getPhoneNumber(),
                userEntity.getStatus(),
                userEntity.getRightGroupIds(),
                userEntity.getOwnerId(),
                Instant.now());
        if (updatedRecordCount == 0) {
            String message = "Failed to update user record";
            log.error(message);
            throw new RecordUpdateFailed(message);
        }
        return user;
    }

    @Override
    public User updatePassword(User user) {
        var updatedRecordCount = userDataRepository.updateUserPassword(user.getId().getId(), user.getPassword().getValue(), Instant.now());
        if (updatedRecordCount == 0) {
            String message = "Failed to update user password";
            log.error(message);
            throw new RecordUpdateFailed(message);
        }
        return user;
    }

    @Override
    public void delete(User user) {
        userDataRepository.deleteUserPassword(user.getId().getId(), Instant.now());
    }

    @Override
    public Optional<User> findById(UserId userId) {
        return userDataRepository.findUserById(userId.getId())
                .map(userDataAccessMapper::transformUserEntityToUser);
    }

    @Override
    public Optional<User> findByUsername(Username username) {
        return userDataRepository.findUserByUsername(username.getValue())
                .map(userDataAccessMapper::transformUserEntityToUser);
    }

    @Override
    public Optional<List<User>> findAll(int pageNumber, int pageSize) {
        return Optional.ofNullable(userDataRepository
                        .findAllUsers(DataAccessHelper.buildPageable(pageNumber, pageSize)))
                .filter(Page::hasContent)
                .map(page -> page.stream()
                        .map(userDataAccessMapper::transformUserEntityToUser)
                        .toList());
    }

    @Override
    public long countAll() {
        return userDataRepository.countAllUsers();
    }

    @Override
    public Optional<List<User>> findAllByUserType(UserType userType, int pageNumber, int pageSize) {
        return Optional.ofNullable(userDataRepository
                        .findAllUsers(userType.getValue(), DataAccessHelper.buildPageable(pageNumber, pageSize)))
                .filter(Page::hasContent)
                .map(page -> page.stream()
                        .map(userDataAccessMapper::transformUserEntityToUser)
                        .toList());
    }

    @Override
    public long countAllByUserType(UserType userType) {
        return userDataRepository.countAllUsers(userType.getValue());
    }

    @Override
    public Optional<List<User>> findAllByOwnerId(OwnerId ownerId, int pageNumber, int pageSize) {
        return Optional.ofNullable(userDataRepository
                        .findAllUsers(ownerId.getId(), DataAccessHelper.buildPageable(pageNumber, pageSize)))
                .filter(Page::hasContent)
                .map(page -> page.stream()
                        .map(userDataAccessMapper::transformUserEntityToUser)
                        .toList());
    }

    @Override
    public long countAllByOwnerId(OwnerId ownerId) {
        return userDataRepository.countAllUsers(ownerId.getId());
    }

    @Override
    public Optional<List<User>> findAllBySearchTerm(String searchTerm, int pageNumber, int pageSize) {
        return Optional.ofNullable(userDataRepository
                        .findAllUsers(searchTerm.toLowerCase(), DataAccessHelper.buildPageable(pageNumber, pageSize)))
                .filter(Page::hasContent)
                .map(page -> page.stream()
                        .map(userDataAccessMapper::transformUserEntityToUser)
                        .toList());
    }

    @Override
    public long countAllBySearchTerm(String searchTerm) {
        return userDataRepository.countAllUsers(searchTerm.toLowerCase());
    }

    @Override
    public Optional<List<User>> findAllByOwnerIdAndSearchTerm(OwnerId ownerId, String searchTerm, int pageNumber, int pageSize) {
        return Optional.ofNullable(userDataRepository
                        .findAllUsers(ownerId.getId(), searchTerm.toLowerCase(), DataAccessHelper.buildPageable(pageNumber, pageSize)))
                .filter(Page::hasContent)
                .map(page -> page.stream()
                        .map(userDataAccessMapper::transformUserEntityToUser)
                        .toList());
    }

    @Override
    public long countAllByOwnerIdAndSearchTerm(OwnerId ownerId, String searchTerm) {
        return userDataRepository.countAllUsers(ownerId.getId(), searchTerm.toLowerCase());
    }
}
