package com.smis.user.domain;

import com.smis.common.core.exception.AccessDenied;
import com.smis.common.core.exception.DomainException;
import com.smis.common.core.util.Right;
import com.smis.common.core.util.Role;
import com.smis.common.core.valueobject.ExecutionUser;
import com.smis.common.core.valueobject.UserId;
import com.smis.common.core.valueobject.UserRight;
import com.smis.common.core.valueobject.UserType;
import com.smis.user.domain.entity.RightGroup;
import com.smis.user.domain.entity.User;
import com.smis.user.domain.exception.UserDomainException;
import com.smis.user.domain.util.Status;
import com.smis.user.domain.valueobject.*;
import org.junit.jupiter.api.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.smis.user.domain.entity.RightGroup.USER_NOT_ALLOWED_TO_MODIFY_RIGHT_GROUP;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserDomainCoreTest {

    private Set<UserRight> SYSTEM_WIDE_RIGHTS_MANAGED_BY_OWNER = Set.of(new UserRight(Right.ACCOUNTING_PAYMENT_VOUCHER_CREATE));
    private final static UUID RIGHT_GROUP_ID_1 = UUID.fromString("4e856209-70d4-46b2-a147-794bbe181587"),
            RIGHT_GROUP_ID_2 = UUID.fromString("5a591a8b-a7e8-4fb4-af59-6996d3a13ac2"),
            SU_ID = UUID.fromString("59f92954-aa2e-4a11-abb3-98e09f393c30"),
            ADMIN_ID_EXEC = UUID.fromString("dc486c2b-4c7c-4e3e-9962-7baedd5ad161"),
            ADMIN_ID = UUID.fromString("6451c6e2-f642-4c4d-8078-808c8d2e1c6c"),
            OWNER_ID_EXEC = UUID.fromString("c9b52d5c-f818-45e4-84b3-4842dae8268c"),
            OWNER_ID_1 = UUID.fromString("e6348a7a-044d-4c3d-8d8b-b2d8fec0e510"),
            OWNER_ID_2 = UUID.fromString("111ae10f-d2d6-479d-8b24-0bbef34b600a"),
            NORMAL_ID_EXEC = UUID.fromString("b4c872ff-d2af-4234-aa3e-adcd807e54ba"),
            NORMAL_ID_1 = UUID.fromString("017a7e8d-c614-4293-bd52-b07ec31d113a"),
            NORMAL_ID_2 = UUID.fromString("629c0378-2268-4386-a3b4-cf02a5dfe76d"),
            NORMAL_ID_3 = UUID.fromString("0eef220e-a670-4100-a498-59edc6c9a984");
    private final static String PASSWORD = "_kSB2b#9w0Ht", RIGHT_GROUP_NAME_1 = "super admin group",
            RIGHT_GROUP_NAME_2 = "admin group";
    private final static RightGroupId RIGHT_GROUP_ID_OBJ_1 = new RightGroupId(RIGHT_GROUP_ID_1);
    private final static RightGroupId RIGHT_GROUP_ID_OBJ_2 = new RightGroupId(RIGHT_GROUP_ID_2);
    private final static RightGroup RIGHT_GROUP_1 = RightGroup.builder()
            .rightGroupId(RIGHT_GROUP_ID_OBJ_1)
            .name(RIGHT_GROUP_NAME_1)
            .rights(List.of(new UserRight(Right.USER_DELETE), new UserRight(Right.USER_CREATE)))
            .build(), RIGHT_GROUP_2 = RightGroup.builder()
            .rightGroupId(RIGHT_GROUP_ID_OBJ_2)
            .name(RIGHT_GROUP_NAME_2)
            .rights(List.of(new UserRight(Right.USER_UPDATE), new UserRight(Right.USER_READ)))
            .build();
    private final static OwnerId OWNER_1 = new OwnerId(OWNER_ID_1), OWNER_2 = new OwnerId(OWNER_ID_2);
    private final static ExecutionUser
            EXECUTION_USER_SU = new ExecutionUser(new UserId(SU_ID), new UserType(Role.SU)),
            EXECUTION_USER_SU_1 = new ExecutionUser(new UserId(UUID.fromString("ea3ee0e7-9e0b-4a01-b9e5-bc1d606d3cb2")), new UserType(Role.SU)),
            EXECUTION_USER_ADMIN = new ExecutionUser(new UserId(ADMIN_ID_EXEC), new UserType(Role.ADMIN)),
            EXECUTION_USER_OWNER = new ExecutionUser(new UserId(OWNER_ID_EXEC), new UserType(Role.OWNER)),
            EXECUTION_USER_OWNER_1 = new ExecutionUser(new UserId(OWNER_ID_1), new UserType(Role.OWNER)),
            EXECUTION_USER_OWNER_2 = new ExecutionUser(new UserId(OWNER_ID_2), new UserType(Role.OWNER)),
            EXECUTION_USER_NORMAL = new ExecutionUser(new UserId(NORMAL_ID_EXEC), new UserType(Role.NORMAL)),
            EXECUTION_USER_NORMAL_1 = new ExecutionUser(new UserId(NORMAL_ID_1), new UserType(Role.NORMAL));
    private static User su, admin, owner_1, owner_2, normal_1, normal_2, normal_3;

    @BeforeAll
    static void init() {
        su = User.builder()
                .userType(new UserType(Role.SU))
                .userId(new UserId(SU_ID))
                .username(new Username("janedoe"))
                .firstname(new Firstname("Jane"))
                .otherNames(new OtherNames("Doe"))
                .password(new Password(PASSWORD))
                .email(new Email("janedoe@mail.com"))
                .phoneNumber(new PhoneNumber(238_884_482_234L))
                .userStatus(new UserStatus(Status.ACTIVE))
                .rightGroups(List.of(RIGHT_GROUP_1))
                .build();

        admin = User.builder()
                .userType(new UserType(Role.ADMIN))
                .userId(new UserId(ADMIN_ID))
                .username(new Username("johdoe"))
                .firstname(new Firstname("John"))
                .otherNames(new OtherNames("Doe"))
                .password(new Password(PASSWORD))
                .email(new Email("johdoe@gmail.com"))
                .phoneNumber(new PhoneNumber(578_942_348_129L))
                .userStatus(new UserStatus(Status.ACTIVE))
                .rightGroups(List.of(RIGHT_GROUP_2))
                .build();

        owner_1 = User.builder()
                .userType(new UserType(Role.OWNER))
                .userId(new UserId(OWNER_1.getId()))
                .username(new Username("peterpan"))
                .firstname(new Firstname("Peter"))
                .otherNames(new OtherNames("Pan"))
                .password(new Password(PASSWORD))
                .email(new Email("peterpan@gmail.com"))
                .phoneNumber(new PhoneNumber(578_942_348_129L))
                .userStatus(new UserStatus(Status.ACTIVE))
                .rightGroups(List.of(RIGHT_GROUP_1))
                .build();

        owner_2 = User.builder()
                .userType(new UserType(Role.OWNER))
                .userId(new UserId(OWNER_2.getId()))
                .username(new Username("isaacnewton"))
                .firstname(new Firstname("Isaac"))
                .otherNames(new OtherNames("Newton"))
                .password(new Password(PASSWORD))
                .email(new Email("isaacnewton@mail.com"))
                .phoneNumber(new PhoneNumber(589_532_958_901L))
                .userStatus(new UserStatus(Status.ACTIVE))
                .rightGroups(List.of(RIGHT_GROUP_1))
                .build();

        normal_1 = User.builder()
                .userType(new UserType(Role.NORMAL))
                .userId(new UserId(NORMAL_ID_1))
                .username(new Username("dinnerwith"))
                .firstname(new Firstname("Dinner"))
                .otherNames(new OtherNames("With"))
                .password(new Password(PASSWORD))
                .email(new Email("dinnerwith@mail.com"))
                .phoneNumber(new PhoneNumber(582_184_902_234L))
                .userStatus(new UserStatus(Status.ACTIVE))
                .rightGroups(List.of(RIGHT_GROUP_1))
                .ownerId(OWNER_1)
                .build();

        normal_2 = User.builder()
                .userType(new UserType(Role.NORMAL))
                .userId(new UserId(NORMAL_ID_2))
                .username(new Username("picknow"))
                .firstname(new Firstname("Pick"))
                .otherNames(new OtherNames("Now"))
                .password(new Password(PASSWORD))
                .email(new Email("picknow@mail.com"))
                .phoneNumber(new PhoneNumber(892_950_134_763L))
                .userStatus(new UserStatus(Status.ACTIVE))
                .rightGroups(List.of(RIGHT_GROUP_1))
                .ownerId(OWNER_2)
                .build();

        normal_3 = User.builder()
                .userType(new UserType(Role.NORMAL))
                .userId(new UserId(NORMAL_ID_3))
                .username(new Username("bzent"))
                .firstname(new Firstname("Bze"))
                .otherNames(new OtherNames("Ent"))
                .password(new Password(PASSWORD))
                .email(new Email("bzent@mail.com"))
                .phoneNumber(new PhoneNumber(893_901_348_781L))
                .userStatus(new UserStatus(Status.ACTIVE))
                .rightGroups(List.of(RIGHT_GROUP_1))
                .ownerId(OWNER_1)
                .build();
    }

    @Test
    void valueObject_Email_success() {
        Email email = new Email("janedoe@mail.com");
        assertEquals("janedoe@mail.com", email.getValue());
    }

    @Test
    void valueObject_Email_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class, () -> new Email("janedoemail.com"));
        assertEquals("Invalid email", exception.getMessage());
    }

    @Test
    void valueObject_ExecutionUser_success() {
        UserId customUserId = new UserId(UUID.randomUUID());
        UserType customUserType = new UserType(Role.OWNER);
        ExecutionUser executionUser = new ExecutionUser(customUserId, customUserType);
        assertEquals(customUserId, executionUser.getUserId());
        assertEquals(customUserType, executionUser.getUserType());
    }

    @Test
    void valueObject_ExecutionUser_fail() {
        UserId customUserId = new UserId(UUID.randomUUID());
        UserType customUserType = new UserType(Role.OWNER);

        DomainException exception1 = assertThrows(DomainException.class, () -> new ExecutionUser(null, customUserType));
        assertEquals("User Id cannot be null", exception1.getMessage());

        DomainException exception2 = assertThrows(DomainException.class, () -> new ExecutionUser(customUserId, null));
        assertEquals("User type is required", exception2.getMessage());
    }

    @Test
    void valueObject_Firstname_success() {
        Firstname firstname = new Firstname("jane");
        assertEquals("jane", firstname.getValue());
    }

    @Test
    void valueObject_Firstname_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class, () -> new Firstname("p"));
        assertEquals("First name must be at-least 3 characters", exception.getMessage());
    }

    @Test
    void valueObject_OtherNames_success() {
        OtherNames otherNames = new OtherNames("issac newton");
        assertEquals("issac newton", otherNames.getValue());
    }

    @Test
    void valueObject_OtherNames_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class, () -> new OtherNames("is"));
        assertEquals("Other names must be at-least 3 characters", exception.getMessage());
    }

    @Test
    void valueObject_OwnerId_success() {
        UUID id = UUID.randomUUID();
        OwnerId ownerId = new OwnerId(id);
        assertEquals(id, ownerId.getId());
    }

    @Test
    void valueObject_OwnerId_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class, () -> new OwnerId(null));
        assertEquals("Owner id is required!", exception.getMessage());
    }

    @Test
    void valueObject_PhoneNumber_success() {
        PhoneNumber phoneNumber = new PhoneNumber(859_235_902_345L);
        assertEquals(859_235_902_345L, phoneNumber.getValue());
    }

    @Test
    void valueObject_PhoneNumber_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class, () -> new PhoneNumber(892_982_942L));
        assertEquals("Phone number must be 12 characters and without special characters", exception.getMessage());
    }

    @Test
    void valueObject_UserId_success() {
        UUID id = UUID.randomUUID();
        UserId userId = new UserId(id);
        assertEquals(id, userId.getId());
    }

    @Test
    void valueObject_UserId_fail() {
        DomainException exception = assertThrows(DomainException.class, () -> new UserId(null));
        assertEquals("User id is required!", exception.getMessage());
    }

    @Test
    void valueObject_Username_success() {
        Username username = new Username("tongue");
        assertEquals("tongue", username.getValue());
    }

    @Test
    void valueObject_Username_fail() {
        UserDomainException exception1 = assertThrows(UserDomainException.class, () -> new Username("   "));
        assertEquals("Username must be at-least 3 characters", exception1.getMessage());
        UserDomainException exception2 = assertThrows(UserDomainException.class, () -> new Username("is"));
        assertEquals("Username must be at-least 3 characters", exception2.getMessage());
    }

    @Test
    void valueObject_UserStatus_success() {
        UserStatus userStatus = new UserStatus(Status.ACTIVE);
        assertEquals(Status.ACTIVE, userStatus.getValue());
    }

    @Test
    void valueObject_UserStatus_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class, () -> new UserStatus(null));
        assertEquals("User status is required", exception.getMessage());
    }

    @Test
    void valueObject_UserType_success() {
        UserType userType = new UserType(Role.ADMIN);
        assertEquals(Role.ADMIN, userType.getValue());
    }

    @Test
    void valueObject_UserType_fail() {
        DomainException exception = assertThrows(DomainException.class, () -> new UserType(null));
        assertEquals("Role is required", exception.getMessage());
    }

    @Test
    @Order(0)
    void testRightGroup_creation() {
        RightGroup rightGroup = RightGroup.builder()
                .build();
        assertNotNull(rightGroup);
        assertNull(rightGroup.getName());
        assertNull(rightGroup.getRights());

    }

    @Test
    @Order(1)
    void testRightGroup_creation_with_rights() {
        RightGroup rightGroup = RightGroup.builder()
                .name("super admin group")
                .rights(List.of(new UserRight(Right.USER_DELETE), new UserRight(Right.USER_CREATE)))
                .build();
        assertEquals(2, rightGroup.getRights().size());

    }

    @Test
    @Order(2)
    void testRightGroup_creation_add_right_invalid_executionUser() {
        RightGroup rightGroup = RightGroup.builder()
                .name("super admin group")
                .rights(List.of(new UserRight(Right.ACCOUNTING_PAYMENT_VOUCHER_CREATE)))
                .build();

        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> rightGroup.addUserRight(EXECUTION_USER_OWNER, SYSTEM_WIDE_RIGHTS_MANAGED_BY_OWNER, new UserRight(Right.ACCOUNTING_PAYMENT_VOUCHER_CREATE)));
        assertEquals("User not allowed to modify right group", exception.getMessage());
    }

    @Test
    @Order(2)
    void testRightGroup_creation_add_duplicate_right() {
        RightGroup rightGroup = RightGroup.builder()
                .name("super admin group")
                .rights(List.of(new UserRight(Right.USER_DELETE), new UserRight(Right.USER_CREATE)))
                .build();
        assertEquals(2, rightGroup.getRights().size());

        //test
        rightGroup.addUserRight(EXECUTION_USER_SU, SYSTEM_WIDE_RIGHTS_MANAGED_BY_OWNER, new UserRight(Right.USER_DELETE));
        assertEquals(2, rightGroup.getRights().size());
    }

    @Test
    @Order(3)
    void testRightGroup_creation_add_non_duplicate_right() {
        RightGroup rightGroup = RightGroup.builder()
                .name("super admin group")
                .rights(List.of(new UserRight(Right.USER_DELETE), new UserRight(Right.USER_CREATE)))
                .build();
        assertEquals(2, rightGroup.getRights().size());

        //test
        rightGroup.addUserRight(EXECUTION_USER_SU, SYSTEM_WIDE_RIGHTS_MANAGED_BY_OWNER, new UserRight(Right.USER_READ));
        assertEquals(3, rightGroup.getRights().size());
    }

    @Test
    @Order(3)
    void testRightGroup_creation_add_with_default_null_right() {
        RightGroup rightGroup = RightGroup.builder()
                .name("super admin group")
                .build();
        assertNull(rightGroup.getRights());

        //test
        rightGroup.addUserRight(EXECUTION_USER_SU, SYSTEM_WIDE_RIGHTS_MANAGED_BY_OWNER, new UserRight(Right.USER_READ));
        assertEquals(1, rightGroup.getRights().size());
        assertEquals(rightGroup.getRights().getFirst(), new UserRight(Right.USER_READ));
    }

    @Test
    @Order(3)
    void testRightGroup_remove_right_invalid_executionUser() {
        RightGroup rightGroup = RightGroup.builder()
                .name("super admin group")
                .rights(List.of(new UserRight(Right.ACCOUNTING_PAYMENT_VOUCHER_CREATE)))
                .build();
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> rightGroup.removeUserRight(EXECUTION_USER_OWNER, SYSTEM_WIDE_RIGHTS_MANAGED_BY_OWNER, new UserRight(Right.USER_READ)));
        assertEquals("User not allowed to modify right group", exception.getMessage());
    }

    @Test
    @Order(3)
    void testRightGroup_remove_right_null() {
        RightGroup rightGroup = RightGroup.builder()
                .name("super admin group")
                .build();
        rightGroup.removeUserRight(EXECUTION_USER_SU, SYSTEM_WIDE_RIGHTS_MANAGED_BY_OWNER, new UserRight(Right.USER_READ));
        assertNull(rightGroup.getRights());
    }

    @Test
    @Order(3)
    void testRightGroup_remove_right_empty() {
        RightGroup rightGroup = RightGroup.builder()
                .name("super admin group")
                .rights(List.of(new UserRight(Right.USER_READ)))
                .build();
        rightGroup.removeUserRight(EXECUTION_USER_SU, SYSTEM_WIDE_RIGHTS_MANAGED_BY_OWNER, new UserRight(Right.USER_READ));
        assertEquals(0, rightGroup.getRights().size());
    }

    @Test
    @Order(3)
    void testRightGroup_remove_right_remainder() {
        RightGroup rightGroup = RightGroup.builder()
                .name("super admin group")
                .rights(List.of(new UserRight(Right.USER_CREATE), new UserRight(Right.USER_READ), new UserRight(Right.USER_UPDATE)))
                .build();
        rightGroup.removeUserRight(EXECUTION_USER_SU, SYSTEM_WIDE_RIGHTS_MANAGED_BY_OWNER, new UserRight(Right.USER_READ));
        assertEquals(2, rightGroup.getRights().size());
    }

    @Test
    @Order(3)
    void testRightGroup_create_no_name_fail() {
        RightGroup rightGroup = RightGroup.builder()
                .build();
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> rightGroup.create(EXECUTION_USER_SU, SYSTEM_WIDE_RIGHTS_MANAGED_BY_OWNER));
        assertEquals("Right group name is required", exception.getMessage());
    }

    @Test
    @Order(3)
    void testRightGroup_create_no_rights_fail() {
        RightGroup rightGroup = RightGroup.builder()
                .name("super admin group")
                .build();
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> rightGroup.create(EXECUTION_USER_SU, SYSTEM_WIDE_RIGHTS_MANAGED_BY_OWNER));
        assertEquals("Right group must have at least one right", exception.getMessage());
    }

    @Test
    @Order(3)
    void testRightGroup_create_success() {
        RightGroup rightGroup = RightGroup.builder()
                .name("super admin group")
                .rights(List.of(new UserRight(Right.USER_CREATE), new UserRight(Right.USER_READ), new UserRight(Right.USER_UPDATE)))
                .build();
        rightGroup.create(EXECUTION_USER_SU, SYSTEM_WIDE_RIGHTS_MANAGED_BY_OWNER);
    }

    @Test
    @Order(3)
    void testRightGroup_create_invalid_executionUser_fail() {
        RightGroup rightGroup = RightGroup.builder()
                .name("owner group")
                .rights(List.of(new UserRight(Right.ACCOUNTING_PAYMENT_VOUCHER_CREATE)))
                .build();
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> rightGroup.create(EXECUTION_USER_OWNER, SYSTEM_WIDE_RIGHTS_MANAGED_BY_OWNER));
        assertEquals("User not allowed to modify right group", exception.getMessage());
    }

    @Test
    @Order(3)
    void testRightGroup_create_null_executionUser_fail() {
        RightGroup rightGroup = RightGroup.builder()
                .name("owner group")
                .rights(List.of(new UserRight(Right.USER_CREATE), new UserRight(Right.USER_READ), new UserRight(Right.USER_UPDATE)))
                .build();
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> rightGroup.create(null, SYSTEM_WIDE_RIGHTS_MANAGED_BY_OWNER));
        assertEquals("User not allowed to modify right group", exception.getMessage());
    }

    @Test
    @Order(3)
    void testRightGroup_delete_invalid_executionUser_fail() {
        RightGroup rightGroup = RightGroup.builder()
                .name("owner group")
                .rights(List.of(new UserRight(Right.ACCOUNTING_PAYMENT_VOUCHER_CREATE)))
                .build();
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> rightGroup.delete(EXECUTION_USER_OWNER, SYSTEM_WIDE_RIGHTS_MANAGED_BY_OWNER));
        assertEquals("User not allowed to modify right group", exception.getMessage());
    }

    @Test
    @Order(3)
    void testRightGroup_delete_invalid_UserRights_For_Owner_fail() {
        RightGroup rightGroup = RightGroup.builder()
                .creatorUserId(EXECUTION_USER_OWNER.getUserId())
                .creatorUserType(EXECUTION_USER_OWNER.getUserType())
                .name("owner group")
                .rights(List.of(new UserRight(Right.USER_CREATE), new UserRight(Right.ACCOUNTING_PAYMENT_VOUCHER_CREATE), new UserRight(Right.USER_UPDATE)))
                .build();

        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class,
                () -> rightGroup.delete(EXECUTION_USER_OWNER, null));
        assertEquals("System wide rights managed by owner must be present", exception2.getMessage());

        IllegalArgumentException exception3 = assertThrows(IllegalArgumentException.class,
                () -> rightGroup.delete(EXECUTION_USER_OWNER, new HashSet<>()));
        assertEquals("System wide rights managed by owner must not be empty", exception3.getMessage());

        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> rightGroup.delete(EXECUTION_USER_OWNER, SYSTEM_WIDE_RIGHTS_MANAGED_BY_OWNER));
        assertEquals("Some rights cannot be manage by " + Role.OWNER, exception.getMessage());
    }

    @Test
    @Order(3)
    void testRightGroup_delete_Mismatch_Owner_As_Creator_fail() {
        RightGroup rightGroup = RightGroup.builder()
                .creatorUserType(new UserType(Role.OWNER))
                .creatorUserId(new UserId(UUID.randomUUID()))
                .name("owner group")
                .rights(List.of(new UserRight(Right.ACCOUNTING_PAYMENT_VOUCHER_CREATE)))
                .build();

        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> rightGroup.delete(EXECUTION_USER_OWNER, SYSTEM_WIDE_RIGHTS_MANAGED_BY_OWNER));
        assertEquals(USER_NOT_ALLOWED_TO_MODIFY_RIGHT_GROUP, exception.getMessage());
    }

    @Test
    @Order(3)
    void testRightGroup_delete_Owner_RightGroup_As_Admin_fail() {
        RightGroup rightGroup = RightGroup.builder()
                .creatorUserId(EXECUTION_USER_OWNER.getUserId())
                .creatorUserType(EXECUTION_USER_OWNER.getUserType())
                .name("owner group")
                .rights(List.of(new UserRight(Right.USER_CREATE), new UserRight(Right.ACCOUNTING_PAYMENT_VOUCHER_CREATE), new UserRight(Right.USER_UPDATE)))
                .build();

        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> rightGroup.delete(EXECUTION_USER_ADMIN, SYSTEM_WIDE_RIGHTS_MANAGED_BY_OWNER));
        assertEquals(USER_NOT_ALLOWED_TO_MODIFY_RIGHT_GROUP, exception.getMessage());
    }

    @Test
    @Order(3)
    void testRightGroup_delete_success() {
        RightGroup rightGroup = RightGroup.builder()
                .creatorUserId(EXECUTION_USER_ADMIN.getUserId())
                .creatorUserType(EXECUTION_USER_ADMIN.getUserType())
                .name("owner group")
                .rights(List.of(new UserRight(Right.USER_CREATE), new UserRight(Right.ACCOUNTING_PAYMENT_VOUCHER_CREATE), new UserRight(Right.USER_UPDATE)))
                .build();

        //as ADMIN delete another admin right group
        rightGroup.delete(EXECUTION_USER_ADMIN, SYSTEM_WIDE_RIGHTS_MANAGED_BY_OWNER);

        //as SU delete another admin right group
        rightGroup.delete(EXECUTION_USER_SU, SYSTEM_WIDE_RIGHTS_MANAGED_BY_OWNER);

        RightGroup rightGroup2 = RightGroup.builder()
                .creatorUserId(EXECUTION_USER_SU.getUserId())
                .creatorUserType(EXECUTION_USER_SU.getUserType())
                .name("owner group")
                .rights(List.of(new UserRight(Right.USER_CREATE), new UserRight(Right.ACCOUNTING_PAYMENT_VOUCHER_CREATE), new UserRight(Right.USER_UPDATE)))
                .build();

        //as SU delete another SU right group
        rightGroup2.delete(EXECUTION_USER_SU_1, SYSTEM_WIDE_RIGHTS_MANAGED_BY_OWNER);

        RightGroup rightGroup3 = RightGroup.builder()
                .creatorUserId(EXECUTION_USER_OWNER.getUserId())
                .creatorUserType(EXECUTION_USER_OWNER.getUserType())
                .name("owner group")
                .rights(List.of(new UserRight(Right.ACCOUNTING_PAYMENT_VOUCHER_CREATE)))
                .build();

        //as OWNER delete own OWNER right group
        rightGroup3.delete(EXECUTION_USER_OWNER, SYSTEM_WIDE_RIGHTS_MANAGED_BY_OWNER);
    }

    @Test
    @Order(4)
    void testUserCreation() {
        User user = User.builder().build();
        assertNotNull(user);
    }

    @Test
    @Order(5)
    void testUserType() {
        User superAdmin = User.builder()
                .userType(new UserType(Role.SU))
                .build();
        assertEquals(new UserType(Role.SU), superAdmin.getUserType());

        User admin = User.builder()
                .userType(new UserType(Role.ADMIN))
                .build();
        assertEquals(new UserType(Role.ADMIN), admin.getUserType());

        User owner = User.builder()
                .userType(new UserType(Role.OWNER))
                .build();
        assertEquals(new UserType(Role.OWNER), owner.getUserType());

        User normalUser = User.builder()
                .userType(new UserType(Role.NORMAL))
                .build();
        assertEquals(new UserType(Role.NORMAL), normalUser.getUserType());
    }

    @Test
    @Order(6)
    void user_AddRightGroup() {
        User normalUser = User.builder()
                .build();
        normalUser.addRightGroup(RIGHT_GROUP_1);
        assertEquals(1, normalUser.getRightGroups().size());
    }

    @Test
    @Order(7)
    void user_AddRightGroup_duplicate() {
        User normalUser = User.builder()
                .rightGroups(List.of(RIGHT_GROUP_1))
                .build();
        normalUser.addRightGroup(RIGHT_GROUP_1);
        assertEquals(1, normalUser.getRightGroups().size());
    }

    @Test
    @Order(8)
    void user_AddRightGroup_non_duplicate() {
        User normalUser = User.builder()
                .rightGroups(List.of(RIGHT_GROUP_1))
                .build();
        normalUser.addRightGroup(RIGHT_GROUP_2);
        assertEquals(2, normalUser.getRightGroups().size());
    }

    @Test
    @Order(9)
    void su_create_all_user_types_success() {
        su.create(EXECUTION_USER_SU);
        admin.create(EXECUTION_USER_SU);
        owner_1.create(EXECUTION_USER_SU);
        owner_2.create(EXECUTION_USER_SU);
    }

    @Test
    @Order(10)
    void admin_create_owner_success() {
        owner_1.create(EXECUTION_USER_ADMIN);
    }

    @Test
    @Order(11)
    void admin_create_su_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> su.create(EXECUTION_USER_ADMIN));
        assertEquals("Admin not allowed to perform this action", exception.getMessage());
    }

    @Test
    @Order(12)
    void admin_create_admin_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> admin.create(EXECUTION_USER_ADMIN));
        assertEquals("Admin not allowed to perform this action", exception.getMessage());
    }

    @Test
    @Order(13)
    void admin_create_normal_user_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> normal_1.create(EXECUTION_USER_ADMIN));
        assertEquals("Admin not allowed to perform this action", exception.getMessage());
    }

    @Test
    @Order(14)
    void owner_create_normal_user_success() {
        normal_1.create(EXECUTION_USER_OWNER_1);
    }

    @Test
    @Order(15)
    void owner_create_normal_user_invalid_owner_fail() {
        UserDomainException exception1 = assertThrows(UserDomainException.class,
                () -> normal_1.create(EXECUTION_USER_OWNER));
        UserDomainException exception2 = assertThrows(UserDomainException.class,
                () -> normal_1.create(EXECUTION_USER_OWNER_2));
        assertEquals("Owner not allowed to perform this action", exception1.getMessage());
        assertEquals("Owner not allowed to perform this action", exception2.getMessage());
    }

    @Test
    @Order(16)
    void owner_create_admin_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> admin.create(EXECUTION_USER_OWNER));
        assertEquals("Owner not allowed to perform this action", exception.getMessage());
    }

    @Test
    @Order(17)
    void owner_create_su_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> su.create(EXECUTION_USER_OWNER));
        assertEquals("Owner not allowed to perform this action", exception.getMessage());
    }

    @Test
    @Order(18)
    void normal_create_normal_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> normal_1.create(EXECUTION_USER_NORMAL));
        assertEquals("User not allowed to perform this action", exception.getMessage());
    }

    @Test
    @Order(19)
    void normal_create_owner_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> owner_1.create(EXECUTION_USER_NORMAL));
        assertEquals("User not allowed to perform this action", exception.getMessage());
    }

    @Test
    @Order(20)
    void normal_create_admin_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> admin.create(EXECUTION_USER_NORMAL));
        assertEquals("User not allowed to perform this action", exception.getMessage());
    }

    @Test
    @Order(21)
    void normal_create_su_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> su.create(EXECUTION_USER_NORMAL));
        assertEquals("User not allowed to perform this action", exception.getMessage());
    }

    @Test
    @Order(22)
    void su_update_all_user_types_success() {
        su.update(EXECUTION_USER_SU);
        admin.update(EXECUTION_USER_SU);
        owner_1.update(EXECUTION_USER_SU);
        owner_2.update(EXECUTION_USER_SU);
        normal_1.update(EXECUTION_USER_SU);
        normal_2.update(EXECUTION_USER_SU);
        normal_3.update(EXECUTION_USER_SU);
    }


    @Test
    @Order(23)
    void admin_update_owner_success() {
        owner_1.update(EXECUTION_USER_ADMIN);
    }

    @Test
    @Order(24)
    void admin_update_su_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> su.update(EXECUTION_USER_ADMIN));
        assertEquals("Admin not allowed to perform this action", exception.getMessage());
    }

    @Test
    @Order(25)
    void admin_update_admin_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> admin.update(EXECUTION_USER_ADMIN));
        assertEquals("Admin not allowed to perform this action", exception.getMessage());
    }

    @Test
    @Order(26)
    void admin_update_normal_user_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> normal_1.update(EXECUTION_USER_ADMIN));
        assertEquals("Admin not allowed to perform this action", exception.getMessage());
    }

    @Test
    @Order(27)
    void owner_update_normal_user_success() {
        normal_1.update(EXECUTION_USER_OWNER_1);
    }

    @Test
    @Order(28)
    void owner_update_normal_user_invalid_owner_fail() {
        UserDomainException exception1 = assertThrows(UserDomainException.class,
                () -> normal_1.update(EXECUTION_USER_OWNER));
        UserDomainException exception2 = assertThrows(UserDomainException.class,
                () -> normal_1.update(EXECUTION_USER_OWNER_2));
        assertEquals("Owner not allowed to perform this action", exception1.getMessage());
        assertEquals("Owner not allowed to perform this action", exception2.getMessage());
    }


    @Test
    @Order(29)
    void owner_update_admin_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> admin.update(EXECUTION_USER_OWNER));
        assertEquals("Owner not allowed to perform this action", exception.getMessage());
    }

    @Test
    @Order(30)
    void owner_update_su_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> su.update(EXECUTION_USER_OWNER));
        assertEquals("Owner not allowed to perform this action", exception.getMessage());
    }

    @Test
    @Order(31)
    void normal_update_normal_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> normal_1.update(EXECUTION_USER_NORMAL));
        assertEquals("User not allowed to perform this action", exception.getMessage());
    }

    @Test
    @Order(32)
    void normal_update_owner_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> owner_1.update(EXECUTION_USER_NORMAL));
        assertEquals("User not allowed to perform this action", exception.getMessage());
    }

    @Test
    @Order(33)
    void normal_update_admin_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> admin.update(EXECUTION_USER_NORMAL));
        assertEquals("User not allowed to perform this action", exception.getMessage());
    }

    @Test
    @Order(34)
    void normal_update_su_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> su.update(EXECUTION_USER_NORMAL));
        assertEquals("User not allowed to perform this action", exception.getMessage());
    }

    @Test
    @Order(35)
    void normal_update_su_password_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> su.updatePassword(EXECUTION_USER_NORMAL));
        assertEquals("You are not allowed to change this user's password", exception.getMessage());
    }

    @Test
    @Order(36)
    void normal_update_another_normal_password_success() {
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> normal_1.updatePassword(EXECUTION_USER_NORMAL));
        assertEquals("You are not allowed to change this user's password", exception.getMessage());
    }

    @Test
    @Order(37)
    void normal_update_password_success() {
        User user = User.builder()
                .userType(new UserType(Role.NORMAL))
                .userId(new UserId(NORMAL_ID_1))
                .username(new Username("dinnerwith"))
                .firstname(new Firstname("Dinner"))
                .otherNames(new OtherNames("With"))
                .password(new Password("U8nRskis9*@dhs@4"))
                .email(new Email("dinnerwith@mail.com"))
                .phoneNumber(new PhoneNumber(582_184_902_234L))
                .userStatus(new UserStatus(Status.ACTIVE))
                .rightGroups(List.of(RIGHT_GROUP_1))
                .ownerId(OWNER_1)
                .build();
        user.updatePassword(EXECUTION_USER_NORMAL_1);
    }

    @Test
    @Order(37)
    void normal_update_password_invalidPassword_fail() {
        User user = User.builder()
                .userType(new UserType(Role.NORMAL))
                .userId(new UserId(NORMAL_ID_1))
                .username(new Username("dinnerwith"))
                .firstname(new Firstname("Dinner"))
                .otherNames(new OtherNames("With"))
                .password(new Password("sh8Nsfefs09fs"))
                .email(new Email("dinnerwith@mail.com"))
                .phoneNumber(new PhoneNumber(582_184_902_234L))
                .userStatus(new UserStatus(Status.ACTIVE))
                .rightGroups(List.of(RIGHT_GROUP_1))
                .ownerId(OWNER_1)
                .build();
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> user.updatePassword(EXECUTION_USER_NORMAL_1));
        assertEquals("Password must be at-least 8 characters long with one digit, one lowercase letter, one uppercase letter, no white space and one special character",
                exception.getMessage());
    }

    @Test
    @Order(38)
    void executionUser_Null() {
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> su.create(null));
        assertEquals("Execution user is required", exception.getMessage());
    }

    @Test
    @Order(39)
    void userType_Null() {
        User user = User.builder()
                .userId(new UserId(SU_ID))
                .username(new Username("janedoe"))
                .firstname(new Firstname("Jane"))
                .otherNames(new OtherNames("Doe"))
                .password(new Password(PASSWORD))
                .email(new Email("janedoe@mail.com"))
                .phoneNumber(new PhoneNumber(238_884_482_234L))
                .userStatus(new UserStatus(Status.ACTIVE))
                .rightGroups(List.of(RIGHT_GROUP_1))
                .build();
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> user.create(EXECUTION_USER_SU));
        assertEquals("User type is required", exception.getMessage());
    }

    @Test
    @Order(40)
    void userName_Null() {
        User user = User.builder()
                .userType(new UserType(Role.SU))
                .userId(new UserId(SU_ID))
                .firstname(new Firstname("Jane"))
                .otherNames(new OtherNames("Doe"))
                .password(new Password(PASSWORD))
                .email(new Email("janedoe@mail.com"))
                .phoneNumber(new PhoneNumber(238_884_482_234L))
                .userStatus(new UserStatus(Status.ACTIVE))
                .rightGroups(List.of(RIGHT_GROUP_1))
                .build();
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> user.create(EXECUTION_USER_SU));
        assertEquals("Username is required", exception.getMessage());
    }

    @Test
    @Order(41)
    void firstName_Null() {
        User user = User.builder()
                .userType(new UserType(Role.SU))
                .userId(new UserId(SU_ID))
                .username(new Username("janedoe"))
                .otherNames(new OtherNames("Doe"))
                .password(new Password(PASSWORD))
                .email(new Email("janedoe@mail.com"))
                .phoneNumber(new PhoneNumber(238_884_482_234L))
                .userStatus(new UserStatus(Status.ACTIVE))
                .rightGroups(List.of(RIGHT_GROUP_1))
                .build();
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> user.create(EXECUTION_USER_SU));
        assertEquals("First name is required", exception.getMessage());
    }

    @Test
    @Order(42)
    void otherNames_Null() {
        User user = User.builder()
                .userType(new UserType(Role.SU))
                .userId(new UserId(SU_ID))
                .username(new Username("janedoe"))
                .firstname(new Firstname("Jane"))
                .password(new Password(PASSWORD))
                .email(new Email("janedoe@mail.com"))
                .phoneNumber(new PhoneNumber(238_884_482_234L))
                .userStatus(new UserStatus(Status.ACTIVE))
                .rightGroups(List.of(RIGHT_GROUP_1))
                .build();
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> user.create(EXECUTION_USER_SU));
        assertEquals("Other names are required", exception.getMessage());
    }

    @Test
    @Order(43)
    void password_invalid() {
        User user = User.builder()
                .userType(new UserType(Role.SU))
                .userId(new UserId(SU_ID))
                .username(new Username("janedoe"))
                .firstname(new Firstname("Jane"))
                .otherNames(new OtherNames("Doe"))
                .password(new Password("j838fhdshfs^"))
                .email(new Email("janedoe@mail.com"))
                .phoneNumber(new PhoneNumber(238_884_482_234L))
                .userStatus(new UserStatus(Status.ACTIVE))
                .rightGroups(List.of(RIGHT_GROUP_1))
                .build();
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> user.create(EXECUTION_USER_SU));
        assertEquals("Password must be at-least 8 characters long with one digit, one lowercase letter, one uppercase letter, no white space and one special character", exception.getMessage());
    }

    @Test
    @Order(44)
    void email_invalid() {
        User user = User.builder()
                .userType(new UserType(Role.SU))
                .userId(new UserId(SU_ID))
                .username(new Username("janedoe"))
                .firstname(new Firstname("Jane"))
                .otherNames(new OtherNames("Doe"))
                .password(new Password(PASSWORD))
                .phoneNumber(new PhoneNumber(238_884_482_234L))
                .userStatus(new UserStatus(Status.ACTIVE))
                .rightGroups(List.of(RIGHT_GROUP_1))
                .build();
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> user.create(EXECUTION_USER_SU));
        assertEquals("Email is required", exception.getMessage());
    }

    @Test
    @Order(45)
    void phoneNumber_invalid() {
        User user = User.builder()
                .userType(new UserType(Role.SU))
                .userId(new UserId(SU_ID))
                .username(new Username("janedoe"))
                .firstname(new Firstname("Jane"))
                .otherNames(new OtherNames("Doe"))
                .password(new Password(PASSWORD))
                .email(new Email("janedoe@mail.com"))
                .userStatus(new UserStatus(Status.ACTIVE))
                .rightGroups(List.of(RIGHT_GROUP_1))
                .build();
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> user.create(EXECUTION_USER_SU));
        assertEquals("Phone Number is required", exception.getMessage());
    }

    @Test
    @Order(46)
    void userStatus_invalid() {
        User user = User.builder()
                .userType(new UserType(Role.SU))
                .userId(new UserId(SU_ID))
                .username(new Username("janedoe"))
                .firstname(new Firstname("Jane"))
                .otherNames(new OtherNames("Doe"))
                .password(new Password(PASSWORD))
                .email(new Email("janedoe@mail.com"))
                .phoneNumber(new PhoneNumber(238_884_482_234L))
                .rightGroups(List.of(RIGHT_GROUP_1))
                .build();
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> user.create(EXECUTION_USER_SU));
        assertEquals("User status is required", exception.getMessage());
    }

    @Test
    @Order(47)
    void rightGroups_invalid() {
        User user = User.builder()
                .userType(new UserType(Role.SU))
                .userId(new UserId(SU_ID))
                .username(new Username("janedoe"))
                .firstname(new Firstname("Jane"))
                .otherNames(new OtherNames("Doe"))
                .password(new Password(PASSWORD))
                .email(new Email("janedoe@mail.com"))
                .phoneNumber(new PhoneNumber(238_884_482_234L))
                .userStatus(new UserStatus(Status.ACTIVE))
                .build();
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> user.create(EXECUTION_USER_SU));
        assertEquals("Right groups are required", exception.getMessage());
    }

    @Test
    @Order(48)
    void Su_Not_Allowed_To_Create_Normal_User() {
        User user = User.builder()
                .userType(new UserType(Role.NORMAL))
                .userId(new UserId(NORMAL_ID_1))
                .username(new Username("dinnerwith"))
                .firstname(new Firstname("Dinner"))
                .otherNames(new OtherNames("With"))
                .password(new Password(PASSWORD))
                .email(new Email("dinnerwith@mail.com"))
                .phoneNumber(new PhoneNumber(582_184_902_234L))
                .userStatus(new UserStatus(Status.ACTIVE))
                .rightGroups(List.of(RIGHT_GROUP_1))
                .build();
        AccessDenied exception = assertThrows(AccessDenied.class,
                () -> user.create(EXECUTION_USER_SU));
        assertEquals("Super user not allowed to create a normal user", exception.getMessage());
    }

    @Test
    @Order(49)
    void delete_self_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> su.delete(EXECUTION_USER_SU));
        assertEquals("You are not allowed to delete yourself", exception.getMessage());
    }

    @Test
    @Order(50)
    void su_delete_all_user_types_success() {
        admin.delete(EXECUTION_USER_SU);
        owner_1.delete(EXECUTION_USER_SU);
        owner_2.delete(EXECUTION_USER_SU);
        normal_1.delete(EXECUTION_USER_SU);
        normal_2.delete(EXECUTION_USER_SU);
        normal_3.delete(EXECUTION_USER_SU);
    }

    @Test
    @Order(51)
    void admin_delete_owner_success() {
        owner_1.delete(EXECUTION_USER_ADMIN);
    }

    @Test
    @Order(52)
    void admin_delete_su_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> su.delete(EXECUTION_USER_ADMIN));
        assertEquals("Admin not allowed to perform this action", exception.getMessage());
    }

    @Test
    @Order(53)
    void admin_delete_admin_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> admin.delete(EXECUTION_USER_ADMIN));
        assertEquals("Admin not allowed to perform this action", exception.getMessage());
    }

    @Test
    @Order(54)
    void admin_delete_normal_user_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> normal_1.delete(EXECUTION_USER_ADMIN));
        assertEquals("Admin not allowed to perform this action", exception.getMessage());
    }

    @Test
    @Order(55)
    void owner_delete_normal_user_success() {
        normal_1.delete(EXECUTION_USER_OWNER_1);
    }

    @Test
    @Order(56)
    void owner_delete_normal_user_invalid_owner_fail() {
        UserDomainException exception1 = assertThrows(UserDomainException.class,
                () -> normal_1.delete(EXECUTION_USER_OWNER));
        UserDomainException exception2 = assertThrows(UserDomainException.class,
                () -> normal_1.delete(EXECUTION_USER_OWNER_2));
        assertEquals("Owner not allowed to perform this action", exception1.getMessage());
        assertEquals("Owner not allowed to perform this action", exception2.getMessage());
    }


    @Test
    @Order(57)
    void owner_delete_admin_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> admin.delete(EXECUTION_USER_OWNER));
        assertEquals("Owner not allowed to perform this action", exception.getMessage());
    }

    @Test
    @Order(58)
    void owner_delete_su_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> su.delete(EXECUTION_USER_OWNER));
        assertEquals("Owner not allowed to perform this action", exception.getMessage());
    }


    @Test
    @Order(59)
    void normal_delete_normal_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> normal_1.delete(EXECUTION_USER_NORMAL));
        assertEquals("User not allowed to perform this action", exception.getMessage());
    }

    @Test
    @Order(60)
    void normal_delete_owner_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> owner_1.delete(EXECUTION_USER_NORMAL));
        assertEquals("User not allowed to perform this action", exception.getMessage());
    }

    @Test
    @Order(61)
    void normal_delete_admin_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> admin.delete(EXECUTION_USER_NORMAL));
        assertEquals("User not allowed to perform this action", exception.getMessage());
    }

    @Test
    @Order(62)
    void normal_delete_su_fail() {
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> su.delete(EXECUTION_USER_NORMAL));
        assertEquals("User not allowed to perform this action", exception.getMessage());
    }

}
