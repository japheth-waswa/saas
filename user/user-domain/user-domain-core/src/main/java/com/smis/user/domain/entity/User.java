package com.smis.user.domain.entity;

import com.smis.common.core.entity.AggregateRoot;
import com.smis.common.core.exception.AccessDenied;
import com.smis.common.core.util.Role;
import com.smis.common.core.valueobject.ExecutionUser;
import com.smis.common.core.valueobject.UserId;
import com.smis.common.core.valueobject.UserType;
import com.smis.user.domain.exception.UserDomainException;
import com.smis.user.domain.util.Status;
import com.smis.user.domain.util.UserDomainConstants;
import com.smis.user.domain.valueobject.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

enum ActionType {
    CREATE,
    UPDATE,
    DELETE
}

@Getter
@EqualsAndHashCode(callSuper = false)
public class User extends AggregateRoot<UserId> {

    private final UserType userType;
    private final Username username;
    private final Firstname firstname;
    private final OtherNames otherNames;

    @Setter
    private Password password;
    private final Email email;
    private final PhoneNumber phoneNumber;
    private final UserStatus userStatus;
    private List<RightGroup> rightGroups;
    private final OwnerId ownerId;

    private User(Builder builder) {
        setId(builder.userId);
        userType = builder.userType;
        username = builder.username;
        firstname = builder.firstname;
        otherNames = builder.otherNames;
        password = builder.password;
        email = builder.email;
        phoneNumber = builder.phoneNumber;
        userStatus = builder.userStatus;
        rightGroups = builder.rightGroups;
        ownerId = builder.ownerId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UserId userId;
        private UserType userType;
        private Username username;
        private Firstname firstname;
        private OtherNames otherNames;
        private Password password;
        private Email email;
        private PhoneNumber phoneNumber;
        private UserStatus userStatus;
        private List<RightGroup> rightGroups;
        private OwnerId ownerId;

        private Builder() {
        }

        public Builder userId(UserId val) {
            userId = val;
            return this;
        }

        public Builder userType(UserType val) {
            userType = val;
            return this;
        }

        public Builder username(Username val) {
            username = val;
            return this;
        }

        public Builder firstname(Firstname val) {
            firstname = val;
            return this;
        }

        public Builder otherNames(OtherNames val) {
            otherNames = val;
            return this;
        }

        public Builder password(Password val) {
            password = val;
            return this;
        }

        public Builder email(Email val) {
            email = val;
            return this;
        }

        public Builder phoneNumber(PhoneNumber val) {
            phoneNumber = val;
            return this;
        }

        public Builder userStatus(UserStatus val) {
            userStatus = val;
            return this;
        }

        public Builder rightGroups(List<RightGroup> val) {
            rightGroups = val;
            return this;
        }

        public Builder ownerId(OwnerId val) {
            ownerId = val;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    public void addRightGroup(RightGroup val) {
        if (rightGroups == null) {
            rightGroups = List.of();
        }
        if (!rightGroups.contains(val)) {
            List<RightGroup> updatedRightGroups = new ArrayList<>(rightGroups);
            updatedRightGroups.add(val);
            rightGroups = List.copyOf(updatedRightGroups);
        }
    }

    public void create(ExecutionUser executionUser) {
        validateProperties(executionUser, ActionType.CREATE);
    }

    private void validateProperties(ExecutionUser executionUser, ActionType actionType) {
        validateExecutionUser(executionUser);
        checkExecutionUserAllowedToModify(executionUser, actionType);
        validateUserType();
        validateUsername();
        validateFirstName();
        validateOtherNames();
        if (actionType.equals(ActionType.CREATE)) {
            validatePassword();
        }
        validateEmail();
        validatePhoneNUmber();
        validateUserStatus();
        validateRightGroups();
        validateOwnerId();
    }

    public void update(ExecutionUser executionUser) {
        password = null;
        validateProperties(executionUser, ActionType.UPDATE);
    }

    public void delete(ExecutionUser executionUser) {
        validateProperties(executionUser, ActionType.DELETE);
    }

    public void updatePassword(ExecutionUser executionUser) {
        if (!executionUser.getUserId().equals(super.getId())) {
            throw new UserDomainException("You are not allowed to change this user's password");
        }
        validatePassword();
    }

    public boolean isAllowedToLogin() {
        return userStatus.getValue().equals(Status.ACTIVE);
    }

    private void checkExecutionUserAllowedToModify(ExecutionUser executionUser, ActionType actionType) {

        if (actionType.equals(ActionType.DELETE) && executionUser.getUserId().equals(super.getId())) {
            throw new UserDomainException("You are not allowed to delete yourself");
        }

        //allowed to modify self
        if (executionUser.getUserId().equals(super.getId())) {
            return;
        }

        //SU allowed to modify all users
        if (executionUser.getUserType().getValue().equals(Role.SU)) {
            if(userType.getValue().equals(Role.NORMAL) && actionType.equals(ActionType.CREATE)){
                throw new AccessDenied("Super user not allowed to create a normal user");
            }
            return;
        }

        if (executionUser.getUserType().getValue().equals(Role.ADMIN)) {
            checkActionAllowedByAdmin(executionUser);
            return;
        }

        if (executionUser.getUserType().getValue().equals(Role.OWNER)) {
            checkActionAllowedByOwner(executionUser);
            return;
        }

        throw new UserDomainException("User not allowed to perform this action");
    }


    private void checkActionAllowedByAdmin(ExecutionUser executionUser) {
        //Admin allowed to modify only the owner
        if (userType.getValue().equals(Role.OWNER)) {
            return;
        }
        throw new UserDomainException("Admin not allowed to perform this action");
    }

    private void checkActionAllowedByOwner(ExecutionUser executionUser) {
        //Owner allowed to modify only the normal user
        if (userType.getValue().equals(Role.NORMAL)
                && ownerId.getId().equals(executionUser.getUserId().getId())) {
            return;
        }
        throw new UserDomainException("Owner not allowed to perform this action");
    }

    private void validateExecutionUser(ExecutionUser executionUser) {
        if (executionUser == null) {
            throw new UserDomainException("Execution user is required");
        }
    }

    private void validateUserType() {
        if (userType == null) {
            throw new UserDomainException("User type is required");
        }
    }

    private void validateUsername() {
        if (username == null) {
            throw new UserDomainException("Username is required");
        }
    }

    private void validateFirstName() {
        if (firstname == null) {
            throw new UserDomainException("First name is required");
        }
    }

    private void validateOtherNames() {
        if (otherNames == null) {
            throw new UserDomainException("Other names are required");
        }
    }

    private void validatePassword() {
        if (password == null || !UserDomainConstants.validatePassword(password.getValue())) {
            throw new UserDomainException("Password must be at-least 8 characters long with one digit, one lowercase letter, one uppercase letter, no white space and one special character");
        }
    }

    private void validateEmail() {
        if (email == null) {
            throw new UserDomainException("Email is required");
        }
    }

    private void validatePhoneNUmber() {
        if (phoneNumber == null) {
            throw new UserDomainException("Phone Number is required");
        }
    }

    private void validateUserStatus() {
        if (userStatus == null) {
            throw new UserDomainException("User status is required");
        }
    }

    private void validateRightGroups() {
        if (rightGroups == null || rightGroups.isEmpty()) {
            throw new UserDomainException("Right groups are required");
        }
    }

    private void validateOwnerId() {
        if (userType.getValue().equals(Role.NORMAL) && ownerId == null) {
            throw new UserDomainException("Owner id is required for normal user");
        }
    }

}
