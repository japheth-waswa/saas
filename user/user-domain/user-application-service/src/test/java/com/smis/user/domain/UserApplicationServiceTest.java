package com.smis.user.domain;

import com.smis.common.core.exception.AccessDenied;
import com.smis.common.core.exception.DomainException;
import com.smis.common.core.exception.InvalidLoginCredentials;
import com.smis.common.core.exception.RecordNotFound;
import com.smis.common.core.util.Right;
import com.smis.common.core.util.Role;
import com.smis.common.core.valueobject.ExecutionUser;
import com.smis.common.core.valueobject.UserId;
import com.smis.common.core.valueobject.UserType;
import com.smis.user.domain.dto.rightgroup.GenericRightGroupCommand;
import com.smis.user.domain.dto.rightgroup.RightGroupListResponse;
import com.smis.user.domain.dto.rightgroup.RightGroupResponse;
import com.smis.user.domain.dto.user.*;
import com.smis.user.domain.entity.RightGroup;
import com.smis.user.domain.entity.User;
import com.smis.user.domain.exception.UserDomainException;
import com.smis.user.domain.mapper.RightGroupDataMapper;
import com.smis.user.domain.mapper.UserDataMapper;
import com.smis.user.domain.ports.input.service.RightGroupApplicationService;
import com.smis.user.domain.ports.input.service.UserApplicationService;
import com.smis.user.domain.ports.output.repository.RightGroupRepository;
import com.smis.user.domain.ports.output.repository.UserRepository;
import com.smis.user.domain.util.Status;
import com.smis.user.domain.valueobject.*;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.smis.common.core.util.Helpers.INVALID_LOGIN_CREDENTIALS_MESSAGE;
import static com.smis.user.domain.util.UserDomainConstants.PASSWORD_PATTERN_ERROR_MESSAGE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = UserApplicationTestConfiguration.class)
public class UserApplicationServiceTest {

    @Autowired
    private RightGroupRepository rightGroupRepository;

    @Autowired
    private RightGroupApplicationService rightGroupApplicationService;

    @Autowired
    RightGroupDataMapper rightGroupDataMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserDataMapper userDataMapper;

    @Autowired
    UserApplicationService userApplicationService;

    @Autowired
    PasswordEncoder passwordEncoder;

    private final static UUID RIGHT_GROUP_ID_1 = UUID.fromString("4e856209-70d4-46b2-a147-794bbe181587"),
            RIGHT_GROUP_ID_2 = UUID.fromString("5a591a8b-a7e8-4fb4-af59-6996d3a13ac2"),
            RIGHT_GROUP_ID_3 = UUID.fromString("979759b5-106b-4ee2-9d16-65ede0071de3"),
            SU_ID = UUID.fromString("59f92954-aa2e-4a11-abb3-98e09f393c30"),
            SU_ID_1 = UUID.fromString("9291f691-3365-4932-a37f-2352f6951299"),
            ADMIN_ID_EXEC = UUID.fromString("dc486c2b-4c7c-4e3e-9962-7baedd5ad161"),
            ADMIN_ID = UUID.fromString("6451c6e2-f642-4c4d-8078-808c8d2e1c6c"),
            OWNER_ID_EXEC = UUID.fromString("c9b52d5c-f818-45e4-84b3-4842dae8268c"),
            OWNER_ID = UUID.fromString("1cd83cca-2915-407c-af1b-38990b9c4a9f"),
            OWNER_ID_1 = UUID.fromString("e6348a7a-044d-4c3d-8d8b-b2d8fec0e510"),
            OWNER_ID_2 = UUID.fromString("111ae10f-d2d6-479d-8b24-0bbef34b600a"),
            NORMAL_ID_EXEC = UUID.fromString("b4c872ff-d2af-4234-aa3e-adcd807e54ba"),
            NORMAL_ID = UUID.fromString("514ac5a5-b34e-446f-babe-c278e29a23b9"),
            NORMAL_ID_1 = UUID.fromString("017a7e8d-c614-4293-bd52-b07ec31d113a"),
            NORMAL_ID_2 = UUID.fromString("629c0378-2268-4386-a3b4-cf02a5dfe76d"),
            NORMAL_ID_3 = UUID.fromString("0eef220e-a670-4100-a498-59edc6c9a984");

    private final static ExecutionUser
            EXECUTION_USER_SU = new ExecutionUser(new UserId(SU_ID), new UserType(Role.SU)),
            EXECUTION_USER_SU_1 = new ExecutionUser(new UserId(SU_ID_1), new UserType(Role.SU)),
            EXECUTION_USER_ADMIN = new ExecutionUser(new UserId(ADMIN_ID_EXEC), new UserType(Role.ADMIN)),
            EXECUTION_USER_OWNER = new ExecutionUser(new UserId(OWNER_ID_EXEC), new UserType(Role.OWNER)),
            EXECUTION_USER_OWNER_1 = new ExecutionUser(new UserId(OWNER_ID_1), new UserType(Role.OWNER)),
            EXECUTION_USER_OWNER_2 = new ExecutionUser(new UserId(OWNER_ID_2), new UserType(Role.OWNER)),
            EXECUTION_USER_NORMAL = new ExecutionUser(new UserId(NORMAL_ID_EXEC), new UserType(Role.NORMAL)),
            EXECUTION_USER_NORMAL_1 = new ExecutionUser(new UserId(NORMAL_ID_1), new UserType(Role.NORMAL));
    private final static String
            RIGHT_GROUP_NAME_ADMIN = "Admin Group",
            RIGHT_GROUP_NAME_OWNER = "Owner Group";
    private final static List<Right>
            RIGHTS_ADMIN = List.of(Right.USER_CREATE, Right.ACCOUNTING_PAYMENT_VOUCHER_CREATE, Right.USER_DELETE),
            RIGHTS_OWNER = List.of(Right.USER_READ, Right.USER_UPDATE),
            RIGHTS_OWNER_1 = List.of(Right.ACCOUNTING_PAYMENT_VOUCHER_CREATE);
    private final static GenericRightGroupCommand
            GENERIC_RIGHT_GROUP_COMMAND_ADMIN = new GenericRightGroupCommand(RIGHT_GROUP_NAME_ADMIN, RIGHTS_ADMIN),
            GENERIC_RIGHT_GROUP_COMMAND_ADMIN_1 = new GenericRightGroupCommand(RIGHT_GROUP_NAME_ADMIN, RIGHTS_OWNER_1),
            GENERIC_RIGHT_GROUP_COMMAND_OWNER = new GenericRightGroupCommand(RIGHT_GROUP_NAME_OWNER, RIGHTS_OWNER),
            GENERIC_RIGHT_GROUP_COMMAND_OWNER_1 = new GenericRightGroupCommand(RIGHT_GROUP_NAME_OWNER, RIGHTS_OWNER_1);
    private final static RightGroupResponse
            RIGHT_GROUP_RESPONSE_ADMIN = new RightGroupResponse(RIGHT_GROUP_ID_1, RIGHT_GROUP_NAME_ADMIN, RIGHTS_ADMIN),
            RIGHT_GROUP_RESPONSE_OWNER = new RightGroupResponse(RIGHT_GROUP_ID_2, RIGHT_GROUP_NAME_OWNER, RIGHTS_OWNER),
            RIGHT_GROUP_RESPONSE_OWNER_1 = new RightGroupResponse(RIGHT_GROUP_ID_2, RIGHT_GROUP_NAME_OWNER, RIGHTS_OWNER_1);
    private RightGroup rightGroupSu = null, rightGroupAdmin = null, rightGroupAdmin_1 = null,
            rightGroupOwner = null, rightGroupOwner_1 = null, rightGroupOwner_su = null, rightGroupOwner_admin = null;
    private final int PAGE_NUMBER = 0, PAGE_SIZE = 10;
    private final long TOTAL_COUNT = 2L;
    private final static String USERNAME_SU = "janedoe",
            USERNAME_ADMIN = "peterpan",
            USERNAME_OWNER = "johndoe",
            USERNAME_NORMAL = "smithownens",
            USERNAME_NORMAL_1 = "thikatown";
    private final static GenericUserCommand
            GENERIC_USER_COMMAND_SU = new GenericUserCommand(
            Role.SU,
            USERNAME_SU,
            "jane",
            "doe",
            "janedoe@mail.com",
            254_872_902_872L,
            Status.ACTIVE,
            List.of(RIGHT_GROUP_ID_1, RIGHT_GROUP_ID_2)),
            GENERIC_USER_COMMAND_ADMIN = new GenericUserCommand(
                    Role.ADMIN,
                    USERNAME_ADMIN,
                    "peter",
                    "pan",
                    "peterpan@mail.com",
                    254_958_349_902L,
                    Status.ACTIVE,
                    List.of(RIGHT_GROUP_ID_1)),
            GENERIC_USER_COMMAND_OWNER = new GenericUserCommand(
                    Role.OWNER,
                    USERNAME_OWNER,
                    "john",
                    "doe",
                    "janedoe@mail.com",
                    254_872_902_872L,
                    Status.ACTIVE,
                    List.of(RIGHT_GROUP_ID_2)),
            GENERIC_USER_COMMAND_NORMAL = new GenericUserCommand(
                    Role.NORMAL,
                    USERNAME_NORMAL,
                    "SMITH",
                    "owens",
                    "smithownens@mail.com",
                    254_872_902_872L,
                    Status.ACTIVE,
                    List.of(RIGHT_GROUP_ID_1, RIGHT_GROUP_ID_2)),
            GENERIC_USER_COMMAND_NORMAL_1 = new GenericUserCommand(
                    Role.NORMAL,
                    USERNAME_NORMAL_1,
                    "Thika",
                    "Town",
                    "thikatown@mail.com",
                    254_872_902_872L,
                    Status.INACTIVE,
                    List.of(RIGHT_GROUP_ID_1, RIGHT_GROUP_ID_2));
    private User userSu = null,
            userAdmin = null,
            userOwner = null,
            userNormal = null,
            userNormal_1 = null;
    private final static Names names = new Names("john");
    private final static OwnerId ownerId = new OwnerId(OWNER_ID_1);
    private final static String SU_PASSWORD = "Nu8&^%$#opd7s",
            ADMIN_PASSWORD = "He7S9J@&H46#",
            OWNER_PASSWORD = "4gFP0M@F(K$SR",
            NORMAL_PASSWORD = "M8U#BF89SSh",
            NORMAL_1_PASSWORD = "Y5U#BF89sdDj";

    @BeforeAll
    void setup() {
        //right group
        rightGroupSu = rightGroupDataMapper.transformGenericRightGroupCommandToRightGroup(EXECUTION_USER_SU, GENERIC_RIGHT_GROUP_COMMAND_ADMIN, new RightGroupId(RIGHT_GROUP_ID_1));
        rightGroupAdmin = rightGroupDataMapper.transformGenericRightGroupCommandToRightGroup(EXECUTION_USER_ADMIN, GENERIC_RIGHT_GROUP_COMMAND_ADMIN, new RightGroupId(RIGHT_GROUP_ID_1));
        rightGroupAdmin_1 = rightGroupDataMapper.transformGenericRightGroupCommandToRightGroup(EXECUTION_USER_ADMIN, GENERIC_RIGHT_GROUP_COMMAND_ADMIN_1, new RightGroupId(RIGHT_GROUP_ID_1));
        rightGroupOwner = rightGroupDataMapper.transformGenericRightGroupCommandToRightGroup(EXECUTION_USER_OWNER, GENERIC_RIGHT_GROUP_COMMAND_OWNER, new RightGroupId(RIGHT_GROUP_ID_2));
        rightGroupOwner_1 = rightGroupDataMapper.transformGenericRightGroupCommandToRightGroup(EXECUTION_USER_OWNER, GENERIC_RIGHT_GROUP_COMMAND_OWNER_1, new RightGroupId(RIGHT_GROUP_ID_2));
        rightGroupOwner_su = rightGroupDataMapper.transformGenericRightGroupCommandToRightGroup(EXECUTION_USER_SU, GENERIC_RIGHT_GROUP_COMMAND_OWNER, new RightGroupId(RIGHT_GROUP_ID_2));
        rightGroupOwner_admin = rightGroupDataMapper.transformGenericRightGroupCommandToRightGroup(EXECUTION_USER_ADMIN, GENERIC_RIGHT_GROUP_COMMAND_OWNER, new RightGroupId(RIGHT_GROUP_ID_2));

        when(rightGroupRepository.create(rightGroupSu)).thenReturn(rightGroupSu);
        when(rightGroupRepository.create(rightGroupAdmin)).thenReturn(rightGroupAdmin);
        when(rightGroupRepository.create(rightGroupAdmin_1)).thenReturn(rightGroupAdmin_1);
        when(rightGroupRepository.create(rightGroupOwner)).thenReturn(rightGroupOwner);
        when(rightGroupRepository.create(rightGroupOwner_1)).thenReturn(rightGroupOwner_1);
        when(rightGroupRepository.create(rightGroupOwner_su)).thenReturn(rightGroupOwner_su);
        when(rightGroupRepository.create(rightGroupOwner_admin)).thenReturn(rightGroupOwner_admin);

        when(rightGroupRepository.update(rightGroupAdmin)).thenReturn(rightGroupAdmin);
        when(rightGroupRepository.update(rightGroupOwner)).thenReturn(rightGroupOwner);
        when(rightGroupRepository.update(rightGroupOwner_1)).thenReturn(rightGroupOwner_1);

        when(rightGroupRepository.findById(new RightGroupId(RIGHT_GROUP_ID_1))).thenReturn(Optional.of(rightGroupAdmin));
        when(rightGroupRepository.findById(new RightGroupId(RIGHT_GROUP_ID_2))).thenReturn(Optional.of(rightGroupOwner));
        when(rightGroupRepository.findById(new RightGroupId(RIGHT_GROUP_ID_3))).thenReturn(Optional.of(rightGroupOwner_1));
        when(rightGroupRepository.findByIds(List.of(new RightGroupId(RIGHT_GROUP_ID_1), new RightGroupId(RIGHT_GROUP_ID_2))))
                .thenReturn(Optional.of(List.of(rightGroupAdmin, rightGroupOwner)));
        when(rightGroupRepository.findByIds(List.of(new RightGroupId(RIGHT_GROUP_ID_1))))
                .thenReturn(Optional.of(List.of(rightGroupAdmin)));
        when(rightGroupRepository.findByIds(List.of(new RightGroupId(RIGHT_GROUP_ID_2))))
                .thenReturn(Optional.of(List.of(rightGroupOwner)));
        when(rightGroupRepository.findAll(PAGE_NUMBER, PAGE_SIZE)).thenReturn(Optional.of(List.of(rightGroupAdmin, rightGroupOwner)));
        when(rightGroupRepository.countAll()).thenReturn(TOTAL_COUNT);

        when(rightGroupRepository.findAll(new OwnerId(EXECUTION_USER_OWNER.getUserId().getId()), PAGE_NUMBER, PAGE_SIZE))
                .thenReturn(Optional.of(List.of(rightGroupAdmin, rightGroupOwner, rightGroupOwner_1)));
        when(rightGroupRepository.countAll(new OwnerId(EXECUTION_USER_OWNER.getUserId().getId()))).thenReturn(5L);

        //user
        userSu = userDataMapper.transformGenericUserCommandToUser(GENERIC_USER_COMMAND_SU, new UserId(SU_ID_1),
                List.of(rightGroupAdmin), new Password(passwordEncoder.encode(SU_PASSWORD)),null);
        userAdmin = userDataMapper.transformGenericUserCommandToUser(GENERIC_USER_COMMAND_ADMIN, new UserId(ADMIN_ID),
                List.of(rightGroupAdmin), new Password(passwordEncoder.encode(ADMIN_PASSWORD)),null);
        userOwner = userDataMapper.transformGenericUserCommandToUser(GENERIC_USER_COMMAND_OWNER, new UserId(OWNER_ID),
                List.of(rightGroupOwner), new Password(passwordEncoder.encode(OWNER_PASSWORD)),null);
        userNormal = userDataMapper.transformGenericUserCommandToUser(GENERIC_USER_COMMAND_NORMAL, new UserId(NORMAL_ID),
                List.of(rightGroupAdmin, rightGroupOwner), new Password(passwordEncoder.encode(NORMAL_PASSWORD)),new OwnerId(OWNER_ID_1));
        userNormal_1 = userDataMapper.transformGenericUserCommandToUser(GENERIC_USER_COMMAND_NORMAL_1, new UserId(NORMAL_ID_1),
                List.of(rightGroupAdmin, rightGroupOwner), new Password(passwordEncoder.encode(NORMAL_1_PASSWORD)),new OwnerId(OWNER_ID_1));

        when(userRepository.save(argThat(user -> user != null && user.getUsername().equals(new Username(USERNAME_SU))))).thenReturn(userSu);
        when(userRepository.save(argThat(user -> user != null && user.getUsername().equals(new Username(USERNAME_ADMIN))))).thenReturn(userAdmin);
        when(userRepository.save(argThat(user -> user != null && user.getUsername().equals(new Username(USERNAME_OWNER))))).thenReturn(userOwner);
        when(userRepository.save(argThat(user -> user != null && user.getUsername().equals(new Username(USERNAME_NORMAL))))).thenReturn(userNormal);

        when(userRepository.update(argThat(user -> user != null && user.getId().equals(new UserId(SU_ID_1))))).thenReturn(userSu);
        when(userRepository.update(argThat(user -> user != null && user.getId().equals(new UserId(ADMIN_ID))))).thenReturn(userAdmin);
        when(userRepository.update(argThat(user -> user != null && user.getId().equals(new UserId(OWNER_ID))))).thenReturn(userOwner);
        when(userRepository.update(argThat(user -> user != null && user.getId().equals(new UserId(NORMAL_ID))))).thenReturn(userNormal);

        when(userRepository.updatePassword(argThat(user -> user != null && user.getId().equals(new UserId(SU_ID_1))))).thenReturn(userSu);
        when(userRepository.updatePassword(argThat(user -> user != null && user.getId().equals(new UserId(ADMIN_ID))))).thenReturn(userAdmin);
        when(userRepository.updatePassword(argThat(user -> user != null && user.getId().equals(new UserId(OWNER_ID))))).thenReturn(userOwner);
        when(userRepository.updatePassword(argThat(user -> user != null && user.getId().equals(new UserId(NORMAL_ID))))).thenReturn(userNormal);

        when(userRepository.findById(new UserId(SU_ID_1))).thenReturn(Optional.of(userSu));
        when(userRepository.findById(new UserId(ADMIN_ID))).thenReturn(Optional.of(userAdmin));
        when(userRepository.findById(new UserId(OWNER_ID))).thenReturn(Optional.of(userOwner));
        when(userRepository.findById(new UserId(NORMAL_ID))).thenReturn(Optional.of(userNormal));

        when(userRepository.findByUsername(new Username(USERNAME_SU))).thenReturn(Optional.of(userSu));
        when(userRepository.findByUsername(new Username(USERNAME_ADMIN))).thenReturn(Optional.of(userAdmin));
        when(userRepository.findByUsername(new Username(USERNAME_OWNER))).thenReturn(Optional.of(userOwner));
        when(userRepository.findByUsername(new Username(USERNAME_NORMAL))).thenReturn(Optional.of(userNormal));
        when(userRepository.findByUsername(new Username(USERNAME_NORMAL_1))).thenReturn(Optional.of(userNormal_1));
        when(userRepository.findAll(PAGE_NUMBER, PAGE_SIZE)).thenReturn(Optional.of(List.of(userSu, userAdmin, userOwner)));
        when(userRepository.countAll()).thenReturn(4L);

        when(userRepository.findAllByUserType(new UserType(Role.SU), PAGE_NUMBER, PAGE_SIZE))
                .thenReturn(Optional.of(List.of(userSu)));
        when(userRepository.countAllByUserType(new UserType(Role.SU))).thenReturn(1L);
        when(userRepository.findAllByUserType(new UserType(Role.ADMIN), PAGE_NUMBER, PAGE_SIZE))
                .thenReturn(Optional.of(List.of(userAdmin)));
        when(userRepository.countAllByUserType(new UserType(Role.ADMIN))).thenReturn(1L);
        when(userRepository.findAllByUserType(new UserType(Role.OWNER), PAGE_NUMBER, PAGE_SIZE))
                .thenReturn(Optional.of(List.of(userOwner)));
        when(userRepository.countAllByUserType(new UserType(Role.OWNER))).thenReturn(1L);
        when(userRepository.findAllByUserType(new UserType(Role.NORMAL), PAGE_NUMBER, PAGE_SIZE))
                .thenReturn(Optional.of(List.of(userNormal, userNormal_1)));
        when(userRepository.countAllByUserType(new UserType(Role.NORMAL))).thenReturn(2L);

        when(userRepository.findAllBySearchTerm(names.getValue(), PAGE_NUMBER, PAGE_SIZE))
                .thenReturn(Optional.of(List.of(userAdmin, userNormal)));
        when(userRepository.countAllBySearchTerm(names.getValue())).thenReturn(3L);

        when(userRepository.findAllByOwnerId(ownerId, PAGE_NUMBER, PAGE_SIZE))
                .thenReturn(Optional.of(List.of(userNormal, userNormal_1)));
        when(userRepository.countAllByOwnerId(ownerId)).thenReturn(7L);

        when(userRepository.findAllByOwnerIdAndSearchTerm(ownerId, names.getValue(), PAGE_NUMBER, PAGE_SIZE))
                .thenReturn(Optional.of(List.of(userNormal)));
        when(userRepository.countAllByOwnerIdAndSearchTerm(ownerId, names.getValue())).thenReturn(5L);
    }

    @Test
    @Order(-3)
    void User_Login_Invalid_Input() {
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.login(null));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.login(new LoginPayload(null, null)));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.login(new LoginPayload(USERNAME_SU, null)));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.login(new LoginPayload(null, SU_PASSWORD)));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.login(new LoginPayload(USERNAME_SU, "xyzi")));
    }

    @Test
    @Order(-2)
    void User_Login_Invalid_Credentials() {
        InvalidLoginCredentials exception = assertThrows(InvalidLoginCredentials.class,
                () -> userApplicationService.login(new LoginPayload(USERNAME_SU, NORMAL_PASSWORD)));
        assertEquals(INVALID_LOGIN_CREDENTIALS_MESSAGE, exception.getMessage());

        assertThrows(InvalidLoginCredentials.class,
                () -> userApplicationService.login(new LoginPayload(USERNAME_SU, "hufs*3hffsMshy^1")));

        assertThrows(InvalidLoginCredentials.class,
                () -> userApplicationService.login(new LoginPayload(USERNAME_NORMAL_1, NORMAL_1_PASSWORD)));
    }

    @Test
    @Order(-1)
    void User_Login_Successful() {
        var loginResponseSu = userApplicationService.login(new LoginPayload(USERNAME_SU, SU_PASSWORD));
        log.info("Login response SU: {}", loginResponseSu);
        assertNotNull(loginResponseSu.accessToken());

        var loginResponseAdmin = userApplicationService.login(new LoginPayload(USERNAME_ADMIN, ADMIN_PASSWORD));
        log.info("Login response ADMIN: {}", loginResponseAdmin);
        assertNotNull(loginResponseAdmin.accessToken());

        var loginResponseOwner = userApplicationService.login(new LoginPayload(USERNAME_OWNER, OWNER_PASSWORD));
        log.info("Login response OWNER: {}", loginResponseOwner);
        assertNotNull(loginResponseOwner.accessToken());

        var loginResponseNormal = userApplicationService.login(new LoginPayload(USERNAME_NORMAL, NORMAL_PASSWORD));
        log.info("Login response NORMAL: {}", loginResponseNormal);
        assertNotNull(loginResponseNormal.accessToken());
    }

    @Test
    @Order(0)
    void rightGroup_Create_Invalid_Input() {
        assertThrows(ConstraintViolationException.class,
                () -> rightGroupApplicationService.createRightGroup(null, null));
        assertThrows(ConstraintViolationException.class,
                () -> rightGroupApplicationService.createRightGroup(EXECUTION_USER_SU, null));
        assertThrows(ConstraintViolationException.class,
                () -> rightGroupApplicationService.createRightGroup(EXECUTION_USER_SU, new GenericRightGroupCommand("pete", null)));
        assertThrows(ConstraintViolationException.class,
                () -> rightGroupApplicationService.createRightGroup(EXECUTION_USER_SU, new GenericRightGroupCommand(null, RIGHTS_ADMIN)));
    }

    @Test
    @Order(1)
    void rightGroup_Create_Invalid_ExecutionUser() {
        UserDomainException exception2 = assertThrows(UserDomainException.class,
                () -> rightGroupApplicationService.createRightGroup(EXECUTION_USER_NORMAL, GENERIC_RIGHT_GROUP_COMMAND_ADMIN));
        assertEquals("User not allowed to modify right group", exception2.getMessage());
    }

    @Test
    @Order(2)
    void rightGroup_Create_Success() {
        RightGroupResponse rightGroupResponse1 = rightGroupApplicationService.createRightGroup(EXECUTION_USER_SU, GENERIC_RIGHT_GROUP_COMMAND_ADMIN);
        assertEquals(RIGHT_GROUP_RESPONSE_ADMIN, rightGroupResponse1);
        assertEquals(RIGHT_GROUP_ID_1, rightGroupResponse1.id());
        RightGroupResponse rightGroupResponse2 = rightGroupApplicationService.createRightGroup(EXECUTION_USER_ADMIN, GENERIC_RIGHT_GROUP_COMMAND_ADMIN);
        assertEquals(RIGHT_GROUP_RESPONSE_ADMIN, rightGroupResponse2);
        assertEquals(RIGHT_GROUP_ID_1, rightGroupResponse2.id());

        RightGroupResponse rightGroupResponse3 = rightGroupApplicationService.createRightGroup(EXECUTION_USER_SU, GENERIC_RIGHT_GROUP_COMMAND_OWNER);
        assertEquals(RIGHT_GROUP_RESPONSE_OWNER, rightGroupResponse3);
        assertEquals(RIGHT_GROUP_ID_2, rightGroupResponse3.id());
        RightGroupResponse rightGroupResponse4 = rightGroupApplicationService.createRightGroup(EXECUTION_USER_ADMIN, GENERIC_RIGHT_GROUP_COMMAND_OWNER);
        assertEquals(RIGHT_GROUP_RESPONSE_OWNER, rightGroupResponse4);
        assertEquals(RIGHT_GROUP_ID_2, rightGroupResponse4.id());
    }

    @Test
    @Order(3)
    void rightGroup_Update_Invalid_Input() {
        assertThrows(ConstraintViolationException.class,
                () -> rightGroupApplicationService.updateRightGroup(null, null, null));
        assertThrows(ConstraintViolationException.class,
                () -> rightGroupApplicationService.updateRightGroup(EXECUTION_USER_SU, null, null));
        assertThrows(ConstraintViolationException.class,
                () -> rightGroupApplicationService.updateRightGroup(EXECUTION_USER_SU, null, new GenericRightGroupCommand("pete", null)));
        assertThrows(ConstraintViolationException.class,
                () -> rightGroupApplicationService.updateRightGroup(EXECUTION_USER_SU, new RightGroupId(RIGHT_GROUP_ID_1), new GenericRightGroupCommand("pete", null)));
        assertThrows(ConstraintViolationException.class,
                () -> rightGroupApplicationService.updateRightGroup(EXECUTION_USER_SU, new RightGroupId(RIGHT_GROUP_ID_1), new GenericRightGroupCommand(null, RIGHTS_ADMIN)));
    }


    @Test
    @Order(4)
    void rightGroup_Update_Invalid_ExecutionUser() {
        UserDomainException exception1 = assertThrows(UserDomainException.class,
                () -> rightGroupApplicationService.updateRightGroup(EXECUTION_USER_OWNER, new RightGroupId(RIGHT_GROUP_ID_1), GENERIC_RIGHT_GROUP_COMMAND_ADMIN_1));
        assertEquals("User not allowed to modify right group", exception1.getMessage());
        UserDomainException exception2 = assertThrows(UserDomainException.class,
                () -> rightGroupApplicationService.updateRightGroup(EXECUTION_USER_NORMAL, new RightGroupId(RIGHT_GROUP_ID_1), GENERIC_RIGHT_GROUP_COMMAND_ADMIN));
        assertEquals("User not allowed to modify right group", exception2.getMessage());
    }


    @Test
    @Order(5)
    void rightGroup_Update_Success() {
        RightGroupResponse rightGroupResponse1 = rightGroupApplicationService.updateRightGroup(EXECUTION_USER_SU, new RightGroupId(RIGHT_GROUP_ID_1), GENERIC_RIGHT_GROUP_COMMAND_ADMIN);
        assertEquals(RIGHT_GROUP_RESPONSE_ADMIN, rightGroupResponse1);
        assertEquals(RIGHT_GROUP_ID_1, rightGroupResponse1.id());
        RightGroupResponse rightGroupResponse2 = rightGroupApplicationService.updateRightGroup(EXECUTION_USER_ADMIN, new RightGroupId(RIGHT_GROUP_ID_1), GENERIC_RIGHT_GROUP_COMMAND_ADMIN);
        assertEquals(RIGHT_GROUP_RESPONSE_ADMIN, rightGroupResponse2);
        assertEquals(RIGHT_GROUP_ID_1, rightGroupResponse2.id());

        RightGroupResponse rightGroupResponse3 = rightGroupApplicationService.updateRightGroup(EXECUTION_USER_SU, new RightGroupId(RIGHT_GROUP_ID_2), GENERIC_RIGHT_GROUP_COMMAND_OWNER);
        assertEquals(RIGHT_GROUP_RESPONSE_OWNER, rightGroupResponse3);
        assertEquals(RIGHT_GROUP_ID_2, rightGroupResponse3.id());
        RightGroupResponse rightGroupResponse4 = rightGroupApplicationService.updateRightGroup(EXECUTION_USER_OWNER, new RightGroupId(RIGHT_GROUP_ID_2), GENERIC_RIGHT_GROUP_COMMAND_OWNER_1);
        assertEquals(RIGHT_GROUP_RESPONSE_OWNER_1, rightGroupResponse4);
        assertEquals(RIGHT_GROUP_ID_2, rightGroupResponse4.id());
    }


    @Test
    @Order(6)
    void rightGroup_Delete_Invalid_Input() {
        assertThrows(ConstraintViolationException.class,
                () -> rightGroupApplicationService.deleteRightGroup(null, null));
        assertThrows(ConstraintViolationException.class,
                () -> rightGroupApplicationService.deleteRightGroup(EXECUTION_USER_SU, null));
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> rightGroupApplicationService.deleteRightGroup(EXECUTION_USER_SU, new RightGroupId(null)));
        assertEquals("Right group id cannot be null", exception.getMessage());
    }


    @Test
    @Order(7)
    void rightGroup_Delete_Missing_Record() {
        RecordNotFound exception1 = assertThrows(RecordNotFound.class,
                () -> rightGroupApplicationService.deleteRightGroup(EXECUTION_USER_OWNER, new RightGroupId(UUID.randomUUID())));
        assertEquals("Right group not found", exception1.getMessage());
    }

    @Test
    @Order(8)
    void rightGroup_Delete_Invalid_ExecutionUser() {
        UserDomainException exception1 = assertThrows(UserDomainException.class,
                () -> rightGroupApplicationService.deleteRightGroup(EXECUTION_USER_OWNER, new RightGroupId(RIGHT_GROUP_ID_1)));
        assertEquals("User not allowed to modify right group", exception1.getMessage());
        UserDomainException exception2 = assertThrows(UserDomainException.class,
                () -> rightGroupApplicationService.deleteRightGroup(EXECUTION_USER_NORMAL, new RightGroupId(RIGHT_GROUP_ID_1)));
        assertEquals("User not allowed to modify right group", exception2.getMessage());
    }

    @Test
    @Order(9)
    void rightGroup_Delete_Success() {
        rightGroupApplicationService.deleteRightGroup(EXECUTION_USER_SU, new RightGroupId(RIGHT_GROUP_ID_1));
        rightGroupApplicationService.deleteRightGroup(EXECUTION_USER_ADMIN, new RightGroupId(RIGHT_GROUP_ID_1));

        rightGroupApplicationService.deleteRightGroup(EXECUTION_USER_SU, new RightGroupId(RIGHT_GROUP_ID_2));
        rightGroupApplicationService.deleteRightGroup(EXECUTION_USER_OWNER, new RightGroupId(RIGHT_GROUP_ID_3));
    }


    @Test
    @Order(10)
    void rightGroup_findAll_Invalid_Input() {
        assertThrows(ConstraintViolationException.class,
                () -> rightGroupApplicationService.findAllRightGroups(EXECUTION_USER_SU, -1, 0));
        assertThrows(ConstraintViolationException.class,
                () -> rightGroupApplicationService.findAllRightGroups(EXECUTION_USER_SU, -1, 1));
        assertThrows(ConstraintViolationException.class,
                () -> rightGroupApplicationService.findAllRightGroups(EXECUTION_USER_SU, 0, 0));
    }

    @Test
    @Order(11)
    void rightGroup_findAll_Invalid_PageNumber_PageSize() {
        RecordNotFound exception1 = assertThrows(RecordNotFound.class,
                () -> rightGroupApplicationService.findAllRightGroups(EXECUTION_USER_SU, 0, 5));
        assertEquals("No right groups found", exception1.getMessage());
        RecordNotFound exception2 = assertThrows(RecordNotFound.class,
                () -> rightGroupApplicationService.findAllRightGroups(EXECUTION_USER_SU, 3, 10));
        assertEquals("No right groups found", exception2.getMessage());
    }

    @Test
    @Order(11)
    void rightGroup_findAll_Invalid_Owner_PageNumber_PageSize() {
        RecordNotFound exception1 = assertThrows(RecordNotFound.class,
                () -> rightGroupApplicationService.findAllRightGroups(EXECUTION_USER_OWNER_1, PAGE_NUMBER, PAGE_SIZE));
        assertEquals("No right groups found", exception1.getMessage());
    }

    @Test
    @Order(12)
    void rightGroup_findAll_success() {
        RightGroupListResponse rightGroupListResponse = rightGroupApplicationService.findAllRightGroups(EXECUTION_USER_SU, PAGE_NUMBER, PAGE_SIZE);
        assertEquals(2L, rightGroupListResponse.totalCount());
        assertEquals(2, rightGroupListResponse.rightGroups().size());

        RightGroupListResponse rightGroupListResponseOwner = rightGroupApplicationService.findAllRightGroups(EXECUTION_USER_OWNER, PAGE_NUMBER, PAGE_SIZE);
        assertEquals(5L, rightGroupListResponseOwner.totalCount());
        assertEquals(3, rightGroupListResponseOwner.rightGroups().size());
    }


    @Test
    @Order(13)
    void User_Create_Invalid_Input() {
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.createUser(null, null));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.createUser(EXECUTION_USER_SU, null));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.createUser(EXECUTION_USER_SU,
                        new GenericUserCommand(null, null, null, null, null, 0, null, null)));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.createUser(EXECUTION_USER_SU,
                        new GenericUserCommand(Role.SU, null, null, null, null, 0, null,  null)));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.createUser(EXECUTION_USER_SU,
                        new GenericUserCommand(Role.SU, "johndoe", null, null, null, 0, null,  null)));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.createUser(EXECUTION_USER_SU,
                        new GenericUserCommand(Role.SU, "johndoe", "john", null, null, 0, null,  null)));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.createUser(EXECUTION_USER_SU,
                        new GenericUserCommand(Role.SU, "johndoe", "john", "doe", null, 0, null,  null)));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.createUser(EXECUTION_USER_SU,
                        new GenericUserCommand(Role.SU, "johndoe", "john", "doe", "johndoe@mail.com", 0, null, null)));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.createUser(EXECUTION_USER_SU,
                        new GenericUserCommand(Role.SU, "johndoe", "john", "doe", "johndoe@mail.com", 254_872_902_872L, null,  null)));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.createUser(EXECUTION_USER_SU,
                        new GenericUserCommand(Role.SU, "johndoe", "john", "doe", "johndoe@mail.com", 254_872_902_872L, Status.ACTIVE,  null)));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.createUser(EXECUTION_USER_SU,
                        new GenericUserCommand(Role.SU, "johndoe", "john", "doe", "johndoe", 254_872_902_872L, Status.ACTIVE,
                                List.of(RIGHT_GROUP_ID_1, RIGHT_GROUP_ID_2))));
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.createUser(EXECUTION_USER_SU,
                        new GenericUserCommand(Role.SU, "johndoe", "john", "doe", "johndoe@mail.com", 254_902_872L, Status.ACTIVE,
                                List.of(RIGHT_GROUP_ID_1, RIGHT_GROUP_ID_2))));
        String actualMessage = exception.getMessage().replaceAll(".*: ", "");
        assertEquals("Phone number must be exactly 12 digits long", actualMessage);
    }

    @Test
    @Order(14)
    void User_Create_Invalid_ExecutionUser() {
        //su not allowed to create normal
        AccessDenied exceptionSu = assertThrows(AccessDenied.class,
                () -> userApplicationService.createUser(EXECUTION_USER_SU, GENERIC_USER_COMMAND_NORMAL));
        assertEquals("Super user not allowed to create a normal user", exceptionSu.getMessage());

        // admin not allowed to create su,admin,normal
        UserDomainException exception1 = assertThrows(UserDomainException.class,
                () -> userApplicationService.createUser(EXECUTION_USER_ADMIN, GENERIC_USER_COMMAND_SU));
        assertEquals("Admin not allowed to perform this action", exception1.getMessage());

        UserDomainException exception2 = assertThrows(UserDomainException.class,
                () -> userApplicationService.createUser(EXECUTION_USER_ADMIN, GENERIC_USER_COMMAND_ADMIN));
        assertEquals("Admin not allowed to perform this action", exception2.getMessage());

        UserDomainException exception3 = assertThrows(UserDomainException.class,
                () -> userApplicationService.createUser(EXECUTION_USER_ADMIN, GENERIC_USER_COMMAND_NORMAL));
        assertEquals("Admin not allowed to perform this action", exception3.getMessage());

        // owner not allowed to create su,admin,owner, and invalid owner
        UserDomainException exception4 = assertThrows(UserDomainException.class,
                () -> userApplicationService.createUser(EXECUTION_USER_OWNER, GENERIC_USER_COMMAND_SU));
        assertEquals("Owner not allowed to perform this action", exception4.getMessage());

        UserDomainException exception5 = assertThrows(UserDomainException.class,
                () -> userApplicationService.createUser(EXECUTION_USER_OWNER, GENERIC_USER_COMMAND_ADMIN));
        assertEquals("Owner not allowed to perform this action", exception5.getMessage());

        UserDomainException exception6 = assertThrows(UserDomainException.class,
                () -> userApplicationService.createUser(EXECUTION_USER_OWNER, GENERIC_USER_COMMAND_OWNER));
        assertEquals("Owner not allowed to perform this action", exception6.getMessage());

        // normal not allowed to create su,admin owner and normal
        UserDomainException exception7 = assertThrows(UserDomainException.class,
                () -> userApplicationService.createUser(EXECUTION_USER_NORMAL, GENERIC_USER_COMMAND_SU));
        assertEquals("User not allowed to perform this action", exception7.getMessage());

        UserDomainException exception8 = assertThrows(UserDomainException.class,
                () -> userApplicationService.createUser(EXECUTION_USER_NORMAL, GENERIC_USER_COMMAND_ADMIN));
        assertEquals("User not allowed to perform this action", exception8.getMessage());

        UserDomainException exception9 = assertThrows(UserDomainException.class,
                () -> userApplicationService.createUser(EXECUTION_USER_NORMAL, GENERIC_USER_COMMAND_OWNER));
        assertEquals("User not allowed to perform this action", exception9.getMessage());

        UserDomainException exception10 = assertThrows(UserDomainException.class,
                () -> userApplicationService.createUser(EXECUTION_USER_NORMAL, GENERIC_USER_COMMAND_NORMAL));
        assertEquals("User not allowed to perform this action", exception10.getMessage());

    }

    @Test
    @Order(15)
    void User_Create_Success() {
        //su create all users
        UserResponse userResponseSu = userApplicationService.createUser(EXECUTION_USER_SU, GENERIC_USER_COMMAND_SU);
        assertEquals(SU_ID_1, userResponseSu.userId());
        UserResponse userResponseAdmin = userApplicationService.createUser(EXECUTION_USER_SU, GENERIC_USER_COMMAND_ADMIN);
        assertEquals(ADMIN_ID, userResponseAdmin.userId());
        UserResponse userResponseOwner = userApplicationService.createUser(EXECUTION_USER_SU, GENERIC_USER_COMMAND_OWNER);
        assertEquals(OWNER_ID, userResponseOwner.userId());

        //admin create owner
        UserResponse userResponseOwner2 = userApplicationService.createUser(EXECUTION_USER_ADMIN, GENERIC_USER_COMMAND_OWNER);
        assertEquals(OWNER_ID, userResponseOwner2.userId());

        //owner create normal
        UserResponse userResponseNormal2 = userApplicationService.createUser(EXECUTION_USER_OWNER_1, GENERIC_USER_COMMAND_NORMAL);
        assertEquals(NORMAL_ID, userResponseNormal2.userId());
    }


    @Test
    @Order(16)
    void User_Update_Invalid_Input() {
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.updateUser(null, null, null));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.updateUser(EXECUTION_USER_SU, new UserId(SU_ID_1), null));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.updateUser(EXECUTION_USER_SU, new UserId(SU_ID_1),
                        new GenericUserCommand(null, null, null, null, null, 0, null,  null)));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.updateUser(EXECUTION_USER_SU, new UserId(SU_ID_1),
                        new GenericUserCommand(Role.SU, null, null, null, null, 0, null, null)));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.updateUser(EXECUTION_USER_SU, new UserId(SU_ID_1),
                        new GenericUserCommand(Role.SU, "johndoe", null, null, null, 0, null,  null)));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.updateUser(EXECUTION_USER_SU, new UserId(SU_ID_1),
                        new GenericUserCommand(Role.SU, "johndoe", "john", null, null, 0, null,  null)));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.updateUser(EXECUTION_USER_SU, new UserId(SU_ID_1),
                        new GenericUserCommand(Role.SU, "johndoe", "john", "doe", null, 0, null,  null)));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.updateUser(EXECUTION_USER_SU, new UserId(SU_ID_1),
                        new GenericUserCommand(Role.SU, "johndoe", "john", "doe", "johndoe@mail.com", 0, null,  null)));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.updateUser(EXECUTION_USER_SU, new UserId(SU_ID_1),
                        new GenericUserCommand(Role.SU, "johndoe", "john", "doe", "johndoe@mail.com", 254_872_902_872L, null, null)));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.updateUser(EXECUTION_USER_SU, new UserId(SU_ID_1),
                        new GenericUserCommand(Role.SU, "johndoe", "john", "doe", "johndoe@mail.com", 254_872_902_872L, Status.ACTIVE,  null)));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.updateUser(EXECUTION_USER_SU, new UserId(SU_ID_1),
                        new GenericUserCommand(Role.SU, "johndoe", "john", "doe", "johndoe", 254_872_902_872L, Status.ACTIVE,
                                List.of(RIGHT_GROUP_ID_1, RIGHT_GROUP_ID_2))));
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.updateUser(EXECUTION_USER_SU, new UserId(SU_ID_1),
                        new GenericUserCommand(Role.SU, "johndoe", "john", "doe", "johndoe@mail.com", 254_902_872L, Status.ACTIVE,
                                List.of(RIGHT_GROUP_ID_1, RIGHT_GROUP_ID_2))));
        String actualMessage = exception.getMessage().replaceAll(".*: ", "");
        assertEquals("Phone number must be exactly 12 digits long", actualMessage);
    }


    @Test
    @Order(17)
    void User_Update_Invalid_ExecutionUser() {
        // admin not allowed to update su,admin,normal
        UserDomainException exception1 = assertThrows(UserDomainException.class,
                () -> userApplicationService.updateUser(EXECUTION_USER_ADMIN, new UserId(SU_ID_1), GENERIC_USER_COMMAND_SU));
        assertEquals("Admin not allowed to perform this action", exception1.getMessage());

        UserDomainException exception2 = assertThrows(UserDomainException.class,
                () -> userApplicationService.updateUser(EXECUTION_USER_ADMIN, new UserId(ADMIN_ID), GENERIC_USER_COMMAND_ADMIN));
        assertEquals("Admin not allowed to perform this action", exception2.getMessage());

        UserDomainException exception3 = assertThrows(UserDomainException.class,
                () -> userApplicationService.updateUser(EXECUTION_USER_ADMIN, new UserId(NORMAL_ID), GENERIC_USER_COMMAND_NORMAL));
        assertEquals("Admin not allowed to perform this action", exception3.getMessage());

        // owner not allowed to update su,admin,owner, and invalid owner
        UserDomainException exception4 = assertThrows(UserDomainException.class,
                () -> userApplicationService.updateUser(EXECUTION_USER_OWNER, new UserId(SU_ID_1), GENERIC_USER_COMMAND_SU));
        assertEquals("Owner not allowed to perform this action", exception4.getMessage());

        UserDomainException exception5 = assertThrows(UserDomainException.class,
                () -> userApplicationService.updateUser(EXECUTION_USER_OWNER, new UserId(ADMIN_ID), GENERIC_USER_COMMAND_ADMIN));
        assertEquals("Owner not allowed to perform this action", exception5.getMessage());

        UserDomainException exception6 = assertThrows(UserDomainException.class,
                () -> userApplicationService.updateUser(EXECUTION_USER_OWNER, new UserId(OWNER_ID), GENERIC_USER_COMMAND_OWNER));
        assertEquals("Owner not allowed to perform this action", exception6.getMessage());

        UserDomainException exception11 = assertThrows(UserDomainException.class,
                () -> userApplicationService.updateUser(EXECUTION_USER_OWNER, new UserId(NORMAL_ID), GENERIC_USER_COMMAND_NORMAL));
        assertEquals("Owner not allowed to perform this action", exception11.getMessage());

        // normal not allowed to update su,admin owner and normal
        UserDomainException exception7 = assertThrows(UserDomainException.class,
                () -> userApplicationService.updateUser(EXECUTION_USER_NORMAL, new UserId(SU_ID_1), GENERIC_USER_COMMAND_SU));
        assertEquals("User not allowed to perform this action", exception7.getMessage());

        UserDomainException exception8 = assertThrows(UserDomainException.class,
                () -> userApplicationService.updateUser(EXECUTION_USER_NORMAL, new UserId(ADMIN_ID), GENERIC_USER_COMMAND_ADMIN));
        assertEquals("User not allowed to perform this action", exception8.getMessage());

        UserDomainException exception9 = assertThrows(UserDomainException.class,
                () -> userApplicationService.updateUser(EXECUTION_USER_NORMAL, new UserId(OWNER_ID), GENERIC_USER_COMMAND_OWNER));
        assertEquals("User not allowed to perform this action", exception9.getMessage());

        UserDomainException exception10 = assertThrows(UserDomainException.class,
                () -> userApplicationService.updateUser(EXECUTION_USER_NORMAL, new UserId(NORMAL_ID), GENERIC_USER_COMMAND_NORMAL));
        assertEquals("User not allowed to perform this action", exception10.getMessage());
    }


    @Test
    @Order(18)
    void User_Update_Success() {
        //su update all users
        UserResponse userResponseSu = userApplicationService.updateUser(EXECUTION_USER_SU, new UserId(SU_ID_1), GENERIC_USER_COMMAND_SU);
        assertEquals(SU_ID_1, userResponseSu.userId());
        UserResponse userResponseAdmin = userApplicationService.updateUser(EXECUTION_USER_SU, new UserId(ADMIN_ID), GENERIC_USER_COMMAND_ADMIN);
        assertEquals(ADMIN_ID, userResponseAdmin.userId());
        UserResponse userResponseOwner = userApplicationService.updateUser(EXECUTION_USER_SU, new UserId(OWNER_ID), GENERIC_USER_COMMAND_OWNER);
        assertEquals(OWNER_ID, userResponseOwner.userId());
        UserResponse userResponseNormal = userApplicationService.updateUser(EXECUTION_USER_SU, new UserId(NORMAL_ID), GENERIC_USER_COMMAND_NORMAL);
        assertEquals(NORMAL_ID, userResponseNormal.userId());

        //admin update owner
        UserResponse userResponseOwner2 = userApplicationService.updateUser(EXECUTION_USER_ADMIN, new UserId(OWNER_ID), GENERIC_USER_COMMAND_OWNER);
        assertEquals(OWNER_ID, userResponseOwner2.userId());

        //owner update normal
        UserResponse userResponseNormal2 = userApplicationService.updateUser(EXECUTION_USER_OWNER_1, new UserId(NORMAL_ID), GENERIC_USER_COMMAND_NORMAL);
        assertEquals(NORMAL_ID, userResponseNormal2.userId());
    }


    @Test
    @Order(19)
    void User_UpdatePassword_Invalid_Input() {
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.updateUserPassword(null, null, null));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.updateUserPassword(EXECUTION_USER_SU, new UserId(SU_ID_1), null));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.updateUserPassword(EXECUTION_USER_SU, new UserId(SU_ID_1),
                        new PasswordModifyUserCommand(null)));
        Exception exception = assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.updateUserPassword(EXECUTION_USER_SU, new UserId(SU_ID_1),
                        new PasswordModifyUserCommand("xhu8S")));
        String actualMessage = exception.getMessage().replaceAll(".*: ", "");
        assertEquals(PASSWORD_PATTERN_ERROR_MESSAGE,
                actualMessage);
    }


    @Test
    @Order(20)
    void User_UpdatePassword_Invalid_ExecutionUser() {
        // only allowed to update your own password
        UserDomainException exception1 = assertThrows(UserDomainException.class,
                () -> userApplicationService.updateUserPassword(EXECUTION_USER_SU, new UserId(ADMIN_ID),
                        new PasswordModifyUserCommand("Nu8&^%$#opd7s")));
        assertEquals("You are not allowed to change this user's password", exception1.getMessage());

        UserDomainException exception2 = assertThrows(UserDomainException.class,
                () -> userApplicationService.updateUserPassword(EXECUTION_USER_SU, new UserId(OWNER_ID),
                        new PasswordModifyUserCommand("Nu8&^%$#opd7s")));
        assertEquals("You are not allowed to change this user's password", exception2.getMessage());

        UserDomainException exception3 = assertThrows(UserDomainException.class,
                () -> userApplicationService.updateUserPassword(EXECUTION_USER_SU, new UserId(NORMAL_ID),
                        new PasswordModifyUserCommand("Nu8&^%$#opd7s")));
        assertEquals("You are not allowed to change this user's password", exception3.getMessage());
    }

    @Test
    @Order(21)
    void User_UpdatePassword_MissingUser_Fail() {
        RecordNotFound exception1 = assertThrows(RecordNotFound.class,
                () -> userApplicationService.updateUserPassword(EXECUTION_USER_SU, new UserId(UUID.randomUUID()),
                        new PasswordModifyUserCommand("Nu8&^%$#opd7s")));
        assertEquals("User not found", exception1.getMessage());
    }

    @Test
    @Order(22)
    void User_UpdatePassword_Success() {
        UserResponse userResponse = userApplicationService.updateUserPassword(EXECUTION_USER_SU_1, new UserId(SU_ID_1),
                new PasswordModifyUserCommand("Nu8&^%$#opd7s"));
        assertEquals(SU_ID_1, userResponse.userId());
    }


    @Test
    @Order(23)
    void User_Delete_Invalid_Input() {
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.deleteUser(null, null));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.deleteUser(EXECUTION_USER_SU, null));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.deleteUser(null, new UserId(SU_ID_1)));

        DomainException exception = assertThrows(DomainException.class,
                () -> userApplicationService.deleteUser(EXECUTION_USER_SU, new UserId(null)));
        assertEquals("User id is required!", exception.getMessage());
    }

    @Test
    @Order(24)
    void User_Delete_Invalid_ExecutionUser() {
        // admin not allowed to delete su,admin,normal
        UserDomainException exception1 = assertThrows(UserDomainException.class,
                () -> userApplicationService.deleteUser(EXECUTION_USER_ADMIN, new UserId(SU_ID_1)));
        assertEquals("Admin not allowed to perform this action", exception1.getMessage());

        UserDomainException exception2 = assertThrows(UserDomainException.class,
                () -> userApplicationService.deleteUser(EXECUTION_USER_ADMIN, new UserId(ADMIN_ID)));
        assertEquals("Admin not allowed to perform this action", exception2.getMessage());

        UserDomainException exception3 = assertThrows(UserDomainException.class,
                () -> userApplicationService.deleteUser(EXECUTION_USER_ADMIN, new UserId(NORMAL_ID)));
        assertEquals("Admin not allowed to perform this action", exception3.getMessage());

        // owner not allowed to delete su,admin,owner, and invalid owner
        UserDomainException exception4 = assertThrows(UserDomainException.class,
                () -> userApplicationService.deleteUser(EXECUTION_USER_OWNER, new UserId(SU_ID_1)));
        assertEquals("Owner not allowed to perform this action", exception4.getMessage());

        UserDomainException exception5 = assertThrows(UserDomainException.class,
                () -> userApplicationService.deleteUser(EXECUTION_USER_OWNER, new UserId(ADMIN_ID)));
        assertEquals("Owner not allowed to perform this action", exception5.getMessage());

        UserDomainException exception6 = assertThrows(UserDomainException.class,
                () -> userApplicationService.deleteUser(EXECUTION_USER_OWNER, new UserId(OWNER_ID)));
        assertEquals("Owner not allowed to perform this action", exception6.getMessage());

        UserDomainException exception11 = assertThrows(UserDomainException.class,
                () -> userApplicationService.deleteUser(EXECUTION_USER_OWNER, new UserId(NORMAL_ID)));
        assertEquals("Owner not allowed to perform this action", exception11.getMessage());

        // normal not allowed to delete su,admin owner and normal
        UserDomainException exception7 = assertThrows(UserDomainException.class,
                () -> userApplicationService.deleteUser(EXECUTION_USER_NORMAL, new UserId(SU_ID_1)));
        assertEquals("User not allowed to perform this action", exception7.getMessage());

        UserDomainException exception8 = assertThrows(UserDomainException.class,
                () -> userApplicationService.deleteUser(EXECUTION_USER_NORMAL, new UserId(ADMIN_ID)));
        assertEquals("User not allowed to perform this action", exception8.getMessage());

        UserDomainException exception9 = assertThrows(UserDomainException.class,
                () -> userApplicationService.deleteUser(EXECUTION_USER_NORMAL, new UserId(OWNER_ID)));
        assertEquals("User not allowed to perform this action", exception9.getMessage());

        UserDomainException exception10 = assertThrows(UserDomainException.class,
                () -> userApplicationService.deleteUser(EXECUTION_USER_NORMAL, new UserId(NORMAL_ID)));
        assertEquals("User not allowed to perform this action", exception10.getMessage());
    }

    @Test
    @Order(25)
    void User_Delete_Success() {
        //su delete all users
        userApplicationService.deleteUser(EXECUTION_USER_SU, new UserId(SU_ID_1));
        userApplicationService.deleteUser(EXECUTION_USER_SU, new UserId(ADMIN_ID));
        userApplicationService.deleteUser(EXECUTION_USER_SU, new UserId(OWNER_ID));
        userApplicationService.deleteUser(EXECUTION_USER_SU, new UserId(NORMAL_ID));

        //admin delete owner
        userApplicationService.deleteUser(EXECUTION_USER_ADMIN, new UserId(OWNER_ID));

        //owner delete normal
        userApplicationService.deleteUser(EXECUTION_USER_OWNER_1, new UserId(NORMAL_ID));
    }


    @Test
    @Order(26)
    void User_Find_By_Username_Invalid_Input() {
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.findUser(null, null));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.findUser(EXECUTION_USER_SU, null));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.findUser(null, new Username(USERNAME_SU)));

        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> userApplicationService.findUser(EXECUTION_USER_SU, new Username(null)));
        assertEquals("Username must be at-least 3 characters", exception.getMessage());
    }


    @Test
    @Order(27)
    void User_Find_By_Username_Missing_Failed() {
        RecordNotFound exception = assertThrows(RecordNotFound.class,
                () -> userApplicationService.findUser(EXECUTION_USER_SU, new Username("xyz")));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @Order(28)
    void User_Find_By_Username_Success() {
        UserResponse userResponse1 = userApplicationService.findUser(EXECUTION_USER_SU, new Username(USERNAME_SU));
        assertEquals(SU_ID_1, userResponse1.userId());

        UserResponse userResponse2 = userApplicationService.findUser(EXECUTION_USER_OWNER, new Username(USERNAME_ADMIN));
        assertEquals(ADMIN_ID, userResponse2.userId());

        UserResponse userResponse3 = userApplicationService.findUser(EXECUTION_USER_SU, new Username(USERNAME_OWNER));
        assertEquals(OWNER_ID, userResponse3.userId());

        UserResponse userResponse4 = userApplicationService.findUser(EXECUTION_USER_ADMIN, new Username(USERNAME_NORMAL));
        assertEquals(NORMAL_ID, userResponse4.userId());
    }

    @Test
    @Order(29)
    void User_Find_All_Users_Invalid_Input() {
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.findAllUsers(null, -1, 0));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.findAllUsers(EXECUTION_USER_SU, -1, 0));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.findAllUsers(EXECUTION_USER_SU, 0, 0));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.findAllUsers(EXECUTION_USER_SU, -1, 1));

    }

    @Test
    @Order(30)
    void User_Find_All_Users_As_Not_Super_Admin_Fail() {
        AccessDenied exception1 = assertThrows(AccessDenied.class,
                () -> userApplicationService.findAllUsers(EXECUTION_USER_ADMIN, PAGE_NUMBER, PAGE_SIZE));
        assertEquals("Access Denied, only super user can fetch all users", exception1.getMessage());

        AccessDenied exception2 = assertThrows(AccessDenied.class,
                () -> userApplicationService.findAllUsers(EXECUTION_USER_OWNER, PAGE_NUMBER, PAGE_SIZE));
        assertEquals("Access Denied, only super user can fetch all users", exception2.getMessage());

        AccessDenied exception3 = assertThrows(AccessDenied.class,
                () -> userApplicationService.findAllUsers(EXECUTION_USER_NORMAL, PAGE_NUMBER, PAGE_SIZE));
        assertEquals("Access Denied, only super user can fetch all users", exception3.getMessage());
    }

    @Test
    @Order(31)
    void User_Find_All_Users_As_Super_Admin_Not_Found() {
        RecordNotFound exception1 = assertThrows(RecordNotFound.class,
                () -> userApplicationService.findAllUsers(EXECUTION_USER_SU, 2, PAGE_SIZE));
        assertEquals("Users not found", exception1.getMessage());
    }

    @Test
    @Order(32)
    void User_Find_All_Users_As_Super_Admin_Success() {
        UserListResponse userListResponse = userApplicationService.findAllUsers(EXECUTION_USER_SU, PAGE_NUMBER, PAGE_SIZE);
        assertEquals(4L, userListResponse.totalCount());
        assertEquals(3, userListResponse.users().size());
    }

    @Test
    @Order(33)
    void User_Find_All_Users_By_UserType_Invalid_Input() {
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.findAllUsersByUserType(null, null, -1, 0));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.findAllUsersByUserType(EXECUTION_USER_SU, null, -1, 0));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.findAllUsersByUserType(EXECUTION_USER_SU, new UserType(Role.ADMIN), 0, 0));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.findAllUsersByUserType(EXECUTION_USER_SU, new UserType(Role.ADMIN), -1, 1));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.findAllUsersByUserType(EXECUTION_USER_SU, null, 0, 1));

    }

    @Test
    @Order(34)
    void User_Find_All_Users_By_UserType_Invalid_ExecutionUser() {
        //admin not allowed to fetch su, admin,normal
        AccessDenied exception1 = assertThrows(AccessDenied.class,
                () -> userApplicationService.findAllUsersByUserType(EXECUTION_USER_ADMIN, new UserType(Role.SU), PAGE_NUMBER, PAGE_SIZE));
        assertEquals("Access Denied, only Super User can fetch users with userType: " + Role.SU, exception1.getMessage());

        AccessDenied exception2 = assertThrows(AccessDenied.class,
                () -> userApplicationService.findAllUsersByUserType(EXECUTION_USER_ADMIN, new UserType(Role.ADMIN), PAGE_NUMBER, PAGE_SIZE));
        assertEquals("Access Denied, only Super User can fetch users with userType: " + Role.ADMIN, exception2.getMessage());

        AccessDenied exception3 = assertThrows(AccessDenied.class,
                () -> userApplicationService.findAllUsersByUserType(EXECUTION_USER_ADMIN, new UserType(Role.NORMAL), PAGE_NUMBER, PAGE_SIZE));
        assertEquals("Access Denied, only Owner can fetch users with userType: " + Role.NORMAL, exception3.getMessage());

        //owner not allowed to fetch su, admin,owner
        AccessDenied exception4 = assertThrows(AccessDenied.class,
                () -> userApplicationService.findAllUsersByUserType(EXECUTION_USER_OWNER, new UserType(Role.SU), PAGE_NUMBER, PAGE_SIZE));
        assertEquals("Access Denied, only Super User can fetch users with userType: " + Role.SU, exception4.getMessage());

        AccessDenied exception5 = assertThrows(AccessDenied.class,
                () -> userApplicationService.findAllUsersByUserType(EXECUTION_USER_OWNER, new UserType(Role.ADMIN), PAGE_NUMBER, PAGE_SIZE));
        assertEquals("Access Denied, only Super User can fetch users with userType: " + Role.ADMIN, exception5.getMessage());

        AccessDenied exception6 = assertThrows(AccessDenied.class,
                () -> userApplicationService.findAllUsersByUserType(EXECUTION_USER_OWNER, new UserType(Role.OWNER), PAGE_NUMBER, PAGE_SIZE));
        assertEquals("Access Denied, only Admin can fetch users with userType: " + Role.OWNER, exception6.getMessage());

        //normal not allowed to fetch su, admin,owner,normal
        AccessDenied exception7 = assertThrows(AccessDenied.class,
                () -> userApplicationService.findAllUsersByUserType(EXECUTION_USER_NORMAL, new UserType(Role.SU), PAGE_NUMBER, PAGE_SIZE));
        assertEquals("Access Denied, only Super User can fetch users with userType: " + Role.SU, exception7.getMessage());

        AccessDenied exception8 = assertThrows(AccessDenied.class,
                () -> userApplicationService.findAllUsersByUserType(EXECUTION_USER_NORMAL, new UserType(Role.ADMIN), PAGE_NUMBER, PAGE_SIZE));
        assertEquals("Access Denied, only Super User can fetch users with userType: " + Role.ADMIN, exception8.getMessage());

        AccessDenied exception9 = assertThrows(AccessDenied.class,
                () -> userApplicationService.findAllUsersByUserType(EXECUTION_USER_NORMAL, new UserType(Role.OWNER), PAGE_NUMBER, PAGE_SIZE));
        assertEquals("Access Denied, only Admin can fetch users with userType: " + Role.OWNER, exception9.getMessage());

        AccessDenied exception10 = assertThrows(AccessDenied.class,
                () -> userApplicationService.findAllUsersByUserType(EXECUTION_USER_NORMAL, new UserType(Role.NORMAL), PAGE_NUMBER, PAGE_SIZE));
        assertEquals("Access Denied, only Owner can fetch users with userType: " + Role.NORMAL, exception10.getMessage());
    }

    @Test
    @Order(35)
    void User_Find_All_Users_By_UserType_Not_Found() {
        RecordNotFound exception1 = assertThrows(RecordNotFound.class,
                () -> userApplicationService.findAllUsersByUserType(EXECUTION_USER_SU, new UserType(Role.SU), 2, PAGE_SIZE));
        assertEquals("Users not found", exception1.getMessage());
        RecordNotFound exception2 = assertThrows(RecordNotFound.class,
                () -> userApplicationService.findAllUsersByUserType(EXECUTION_USER_SU, new UserType(Role.ADMIN), PAGE_NUMBER, 30));
        assertEquals("Users not found", exception2.getMessage());
        RecordNotFound exception3 = assertThrows(RecordNotFound.class,
                () -> userApplicationService.findAllUsersByUserType(EXECUTION_USER_SU, new UserType(Role.OWNER), 2, PAGE_SIZE));
        assertEquals("Users not found", exception3.getMessage());
        RecordNotFound exception4 = assertThrows(RecordNotFound.class,
                () -> userApplicationService.findAllUsersByUserType(EXECUTION_USER_SU, new UserType(Role.NORMAL), PAGE_NUMBER, 100));
        assertEquals("Users not found", exception4.getMessage());
    }

    @Test
    @Order(36)
    void User_Find_All_Users_By_UserType_Success() {
        UserListResponse userListResponse1 = userApplicationService.findAllUsersByUserType(EXECUTION_USER_SU, new UserType(Role.SU), PAGE_NUMBER, PAGE_SIZE);
        assertEquals(1L, userListResponse1.totalCount());
        assertEquals(1, userListResponse1.users().size());
        UserListResponse userListResponse2 = userApplicationService.findAllUsersByUserType(EXECUTION_USER_SU, new UserType(Role.ADMIN), PAGE_NUMBER, PAGE_SIZE);
        assertEquals(1L, userListResponse2.totalCount());
        assertEquals(1, userListResponse2.users().size());
        UserListResponse userListResponse3 = userApplicationService.findAllUsersByUserType(EXECUTION_USER_SU, new UserType(Role.OWNER), PAGE_NUMBER, PAGE_SIZE);
        assertEquals(1L, userListResponse3.totalCount());
        assertEquals(1, userListResponse3.users().size());
        UserListResponse userListResponse4 = userApplicationService.findAllUsersByUserType(EXECUTION_USER_SU, new UserType(Role.NORMAL), PAGE_NUMBER, PAGE_SIZE);
        assertEquals(2L, userListResponse4.totalCount());
        assertEquals(2, userListResponse4.users().size());
    }


    @Test
    @Order(37)
    void User_Find_All_Users_By_Names_Invalid_Input() {
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.findAllUsersByNames(null, names, -1, 0));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.findAllUsersByNames(EXECUTION_USER_SU, names, -1, 0));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.findAllUsersByNames(EXECUTION_USER_SU, names, 0, 0));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.findAllUsersByNames(EXECUTION_USER_SU, names, -1, 1));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.findAllUsersByNames(EXECUTION_USER_SU, null, -1, 1));
    }

    @Test
    @Order(38)
    void User_Find_All_Users_By_Names_As_Not_Super_Admin_Fail() {
        AccessDenied exception1 = assertThrows(AccessDenied.class,
                () -> userApplicationService.findAllUsersByNames(EXECUTION_USER_ADMIN, names, PAGE_NUMBER, PAGE_SIZE));
        assertEquals("Access Denied, only super user can fetch all users", exception1.getMessage());

        AccessDenied exception2 = assertThrows(AccessDenied.class,
                () -> userApplicationService.findAllUsersByNames(EXECUTION_USER_OWNER, names, PAGE_NUMBER, PAGE_SIZE));
        assertEquals("Access Denied, only super user can fetch all users", exception2.getMessage());

        AccessDenied exception3 = assertThrows(AccessDenied.class,
                () -> userApplicationService.findAllUsersByNames(EXECUTION_USER_NORMAL, names, PAGE_NUMBER, PAGE_SIZE));
        assertEquals("Access Denied, only super user can fetch all users", exception3.getMessage());
    }

    @Test
    @Order(39)
    void User_Find_All_Users_By_Names_As_Super_Admin_Not_Found() {
        RecordNotFound exception1 = assertThrows(RecordNotFound.class,
                () -> userApplicationService.findAllUsersByNames(EXECUTION_USER_SU, names, 2, PAGE_SIZE));
        assertEquals("Users not found", exception1.getMessage());

        RecordNotFound exception2 = assertThrows(RecordNotFound.class,
                () -> userApplicationService.findAllUsersByNames(EXECUTION_USER_SU, new Names("peter"), PAGE_NUMBER, PAGE_SIZE));
        assertEquals("Users not found", exception2.getMessage());
    }

    @Test
    @Order(40)
    void User_Find_All_Users_By_Names_As_Super_Admin_Success() {
        UserListResponse userListResponse = userApplicationService.findAllUsersByNames(EXECUTION_USER_SU, names, PAGE_NUMBER, PAGE_SIZE);
        assertEquals(3L, userListResponse.totalCount());
        assertEquals(2, userListResponse.users().size());
    }


    @Test
    @Order(41)
    void User_Find_All_Users_By_OwnerId_Invalid_Input() {
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.findAllUsersByOwnerId(null, ownerId, -1, 0));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.findAllUsersByOwnerId(EXECUTION_USER_SU, ownerId, -1, 0));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.findAllUsersByOwnerId(EXECUTION_USER_SU, ownerId, 0, 0));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.findAllUsersByOwnerId(EXECUTION_USER_SU, ownerId, -1, 1));
        UserDomainException exception = assertThrows(UserDomainException.class,
                () -> userApplicationService.findAllUsersByOwnerId(EXECUTION_USER_SU, new OwnerId(null), PAGE_NUMBER, PAGE_SIZE));
        assertEquals("Owner id is required!", exception.getMessage());
    }


    @Test
    @Order(42)
    void User_Find_All_Users_By_OwnerId_Invalid_ExecutionUser() {
        //admin & normal users not allowed to fetch normal
        AccessDenied exception1 = assertThrows(AccessDenied.class,
                () -> userApplicationService.findAllUsersByOwnerId(EXECUTION_USER_ADMIN, ownerId, PAGE_NUMBER, PAGE_SIZE));
        assertEquals("Access Denied, only Owner can fetch users with userType: " + Role.NORMAL, exception1.getMessage());
        AccessDenied exception2 = assertThrows(AccessDenied.class,
                () -> userApplicationService.findAllUsersByOwnerId(EXECUTION_USER_NORMAL, ownerId, PAGE_NUMBER, PAGE_SIZE));
        assertEquals("Access Denied, only Owner can fetch users with userType: " + Role.NORMAL, exception2.getMessage());
    }


    @Test
    @Order(43)
    void User_Find_All_Users_By_OwnerId_Not_Found() {
        RecordNotFound exception1 = assertThrows(RecordNotFound.class,
                () -> userApplicationService.findAllUsersByOwnerId(EXECUTION_USER_SU, ownerId, 2, PAGE_SIZE));
        assertEquals("Users not found", exception1.getMessage());
        RecordNotFound exception2 = assertThrows(RecordNotFound.class,
                () -> userApplicationService.findAllUsersByOwnerId(EXECUTION_USER_OWNER, ownerId, PAGE_NUMBER, 30));
        assertEquals("Users not found", exception2.getMessage());
    }


    @Test
    @Order(44)
    void User_Find_All_Users_By_OwnerId_Success() {
        UserListResponse userListResponse1 = userApplicationService.findAllUsersByOwnerId(EXECUTION_USER_SU, ownerId, PAGE_NUMBER, PAGE_SIZE);
        assertEquals(7L, userListResponse1.totalCount());
        assertEquals(2, userListResponse1.users().size());
        UserListResponse userListResponse2 = userApplicationService.findAllUsersByOwnerId(EXECUTION_USER_SU, ownerId, PAGE_NUMBER, PAGE_SIZE);
        assertEquals(7L, userListResponse2.totalCount());
        assertEquals(2, userListResponse2.users().size());
    }


    @Test
    @Order(45)
    void User_Find_All_Users_By_OwnerId_And_Names_Invalid_Input() {
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.findAllUsersByOwnerIdAndNames(null, ownerId, names, -1, 0));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.findAllUsersByOwnerIdAndNames(EXECUTION_USER_SU, ownerId, names, -1, 0));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.findAllUsersByOwnerIdAndNames(EXECUTION_USER_SU, ownerId, names, 0, 0));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.findAllUsersByOwnerIdAndNames(EXECUTION_USER_SU, ownerId, names, -1, 1));
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.findAllUsersByOwnerIdAndNames(EXECUTION_USER_SU, ownerId, null, -1, 1));
        UserDomainException exception1 = assertThrows(UserDomainException.class,
                () -> userApplicationService.findAllUsersByOwnerIdAndNames(EXECUTION_USER_SU, new OwnerId(null), names, PAGE_NUMBER, PAGE_SIZE));
        assertEquals("Owner id is required!", exception1.getMessage());
        UserDomainException exception2 = assertThrows(UserDomainException.class,
                () -> userApplicationService.findAllUsersByOwnerIdAndNames(EXECUTION_USER_SU, ownerId, new Names(null), PAGE_NUMBER, PAGE_SIZE));
        assertEquals("Names must be at-least 3 characters", exception2.getMessage());
    }


    @Test
    @Order(46)
    void User_Find_All_Users_By_OwnerId_And_Names_Invalid_ExecutionUser() {
        //admin & normal users not allowed to fetch normal
        AccessDenied exception1 = assertThrows(AccessDenied.class,
                () -> userApplicationService.findAllUsersByOwnerIdAndNames(EXECUTION_USER_ADMIN, ownerId, names, PAGE_NUMBER, PAGE_SIZE));
        assertEquals("Access Denied, only Owner can fetch users with userType: " + Role.NORMAL, exception1.getMessage());
        AccessDenied exception2 = assertThrows(AccessDenied.class,
                () -> userApplicationService.findAllUsersByOwnerIdAndNames(EXECUTION_USER_NORMAL, ownerId, names, PAGE_NUMBER, PAGE_SIZE));
        assertEquals("Access Denied, only Owner can fetch users with userType: " + Role.NORMAL, exception2.getMessage());
    }


    @Test
    @Order(47)
    void User_Find_All_Users_By_OwnerId_And_Names_Not_Found() {
        RecordNotFound exception1 = assertThrows(RecordNotFound.class,
                () -> userApplicationService.findAllUsersByOwnerIdAndNames(EXECUTION_USER_SU, ownerId, names, 2, PAGE_SIZE));
        assertEquals("Users not found", exception1.getMessage());
        RecordNotFound exception2 = assertThrows(RecordNotFound.class,
                () -> userApplicationService.findAllUsersByOwnerIdAndNames(EXECUTION_USER_OWNER, ownerId, names, PAGE_NUMBER, 30));
        assertEquals("Users not found", exception2.getMessage());
        RecordNotFound exception3 = assertThrows(RecordNotFound.class,
                () -> userApplicationService.findAllUsersByOwnerIdAndNames(EXECUTION_USER_OWNER, new OwnerId(OWNER_ID_2), names, PAGE_NUMBER, 30));
        assertEquals("Users not found", exception3.getMessage());
        RecordNotFound exception4 = assertThrows(RecordNotFound.class,
                () -> userApplicationService.findAllUsersByOwnerIdAndNames(EXECUTION_USER_OWNER, ownerId, new Names("kelly"), PAGE_NUMBER, 30));
        assertEquals("Users not found", exception4.getMessage());
    }

    @Test
    @Order(48)
    void User_Find_All_Users_By_OwnerId_And_Names_Success() {
        UserListResponse userListResponse1 = userApplicationService.findAllUsersByOwnerIdAndNames(EXECUTION_USER_SU, ownerId, names, PAGE_NUMBER, PAGE_SIZE);
        assertEquals(5L, userListResponse1.totalCount());
        assertEquals(1, userListResponse1.users().size());
        UserListResponse userListResponse2 = userApplicationService.findAllUsersByOwnerIdAndNames(EXECUTION_USER_SU, ownerId, names, PAGE_NUMBER, PAGE_SIZE);
        assertEquals(5L, userListResponse2.totalCount());
        assertEquals(1, userListResponse2.users().size());
    }

}
