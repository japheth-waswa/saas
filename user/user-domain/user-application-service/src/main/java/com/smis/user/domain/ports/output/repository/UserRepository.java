package com.smis.user.domain.ports.output.repository;

import com.smis.common.core.valueobject.UserId;
import com.smis.common.core.valueobject.UserType;
import com.smis.user.domain.entity.User;
import com.smis.user.domain.valueobject.OwnerId;
import com.smis.user.domain.valueobject.Username;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    User update(User user);

    User updatePassword(User user);

    void delete(User user);

    Optional<User> findById(UserId userId);

    Optional<User> findByUsername(Username username);

    Optional<List<User>> findAll(int pageNumber, int pageSize);

    long countAll();

    Optional<List<User>> findAllByUserType(UserType userType, int pageNumber, int pageSize);

    long countAllByUserType(UserType userType);

    Optional<List<User>> findAllByOwnerId(OwnerId ownerId, int pageNumber, int pageSize);

    long countAllByOwnerId(OwnerId ownerId);

    Optional<List<User>> findAllBySearchTerm(String searchTerm, int pageNumber, int pageSize);

    long countAllBySearchTerm(String searchTerm);

    Optional<List<User>> findAllByOwnerIdAndSearchTerm(OwnerId ownerId, String searchTerm, int pageNumber, int pageSize);

    long countAllByOwnerIdAndSearchTerm(OwnerId ownerId, String searchTerm);

}
