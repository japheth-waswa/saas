package com.smis.user.dataaccess;

import com.smis.common.core.exception.DuplicateRecord;
import com.smis.common.core.exception.RecordUpdateFailed;
import com.smis.common.core.util.Right;
import com.smis.common.core.util.Role;
import com.smis.common.core.valueobject.ExecutionUser;
import com.smis.common.core.valueobject.UserId;
import com.smis.common.core.valueobject.UserType;
import com.smis.user.domain.dto.rightgroup.GenericRightGroupCommand;
import com.smis.user.domain.dto.user.GenericUserCommand;
import com.smis.user.domain.entity.RightGroup;
import com.smis.user.domain.entity.User;
import com.smis.user.domain.mapper.RightGroupDataMapper;
import com.smis.user.domain.mapper.UserDataMapper;
import com.smis.user.domain.ports.input.service.UserApplicationService;
import com.smis.user.domain.ports.output.repository.RightGroupRepository;
import com.smis.user.domain.ports.output.repository.UserRepository;
import com.smis.user.domain.util.Status;
import com.smis.user.domain.valueobject.OwnerId;
import com.smis.user.domain.valueobject.Password;
import com.smis.user.domain.valueobject.RightGroupId;
import com.smis.user.domain.valueobject.Username;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.smis.common.core.util.Helpers.ROLE_OWNER_DEFAULT_RIGHT_GROUP_ID;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = UserDataAccessTestConfiguration.class)
public class UserDataAccessTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    RightGroupRepository rightGroupRepository;

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

    private final static UUID
            RIGHT_GROUP_ID_SU = UUID.fromString("1523dbeb-786c-4c06-b6ee-85911fa8d55b"),
            RIGHT_GROUP_ID_1 = ROLE_OWNER_DEFAULT_RIGHT_GROUP_ID,
            RIGHT_GROUP_ID_ADMIN_MIN = UUID.fromString("60dbef4b-fe54-402e-b025-a6c66c0db826"),
            RIGHT_GROUP_ID_2 = UUID.fromString("5a591a8b-a7e8-4fb4-af59-6996d3a13ac2"),
            RIGHT_GROUP_ID_3 = UUID.fromString("979759b5-106b-4ee2-9d16-65ede0071de3"),
            RIGHT_GROUP_ID_4 = UUID.fromString("c73729d9-5081-4625-92f4-efd5178e7ba2"),
            RIGHT_GROUP_ID_5 = UUID.fromString("912df5cd-d342-4a92-8227-3bb22107fd9f"),
            SU_ID = UUID.fromString("59f92954-aa2e-4a11-abb3-98e09f393c30"),
            SU_ID_1 = UUID.fromString("9291f691-3365-4932-a37f-2352f6951299"),
            ADMIN_ID_EXEC = UUID.fromString("dc486c2b-4c7c-4e3e-9962-7baedd5ad161"),
            ADMIN_ID = UUID.fromString("6451c6e2-f642-4c4d-8078-808c8d2e1c6c"),
            ADMIN_ID_EXT = UUID.fromString("b8f8bd55-2a76-47ba-bbdf-5b84176d73c6"),
            OWNER_ID_EXEC = UUID.fromString("c9b52d5c-f818-45e4-84b3-4842dae8268c"),
            OWNER_ID = UUID.fromString("1cd83cca-2915-407c-af1b-38990b9c4a9f"),
            OWNER_ID_1 = UUID.fromString("e6348a7a-044d-4c3d-8d8b-b2d8fec0e510"),
            OWNER_ID_2 = UUID.fromString("111ae10f-d2d6-479d-8b24-0bbef34b600a"),
            NORMAL_ID_Full = UUID.fromString("d12ebeb9-1438-4d08-8c93-baf382088b44"),
            NORMAL_ID_EXEC = UUID.fromString("b4c872ff-d2af-4234-aa3e-adcd807e54ba"),
            NORMAL_ID = UUID.fromString("514ac5a5-b34e-446f-babe-c278e29a23b9"),
            NORMAL_ID_1 = UUID.fromString("017a7e8d-c614-4293-bd52-b07ec31d113a"),
            OWNER_ID_FULL = UUID.fromString("b307010e-a9ea-4e43-b6e4-9df041baed0e"),
            NORMAL_ID_2 = UUID.fromString("629c0378-2268-4386-a3b4-cf02a5dfe76d"),
            NORMAL_ID_3 = UUID.fromString("0eef220e-a670-4100-a498-59edc6c9a984");

    private final static ExecutionUser
            EXECUTION_USER_SU_1 = new ExecutionUser(new UserId(SU_ID_1), new UserType(Role.SU)),
            EXECUTION_USER_ADMIN = new ExecutionUser(new UserId(ADMIN_ID_EXEC), new UserType(Role.ADMIN)),
            EXECUTION_USER_ADMIN_1 = new ExecutionUser(new UserId(ADMIN_ID), new UserType(Role.ADMIN)),
            EXECUTION_USER_OWNER = new ExecutionUser(new UserId(OWNER_ID_EXEC), new UserType(Role.OWNER)),
            EXECUTION_USER_OWNER_2 = new ExecutionUser(new UserId(OWNER_ID_2), new UserType(Role.OWNER));

    private final static String
            RIGHT_GROUP_NAME_ADMIN = "Admin Group",
            RIGHT_GROUP_NAME_OWNER = "Owner Group";

    private final static List<Right>
//            RIGHTS_ADMIN = List.of(Right.USER_CREATE, Right.USER_DELETE),
            RIGHTS_ADMIN = List.of(Right.ACCOUNTING_PAYMENT_VOUCHER_CREATE),
            RIGHTS_ADMIN_FULL = Arrays.stream(Right.values()).toList(),
            RIGHTS_OWNER = List.of(Right.ACCOUNTING_PAYMENT_VOUCHER_CREATE),
            RIGHTS_OWNER_2 = List.of(Right.USER_READ, Right.USER_CREATE, Right.USER_DELETE, Right.USER_UPDATE),
            RIGHTS_NORMAL = List.of(Right.ACCOUNTING_PAYMENT_VOUCHER_CREATE);

    private final static GenericRightGroupCommand
            GENERIC_RIGHT_GROUP_COMMAND_ADMIN = new GenericRightGroupCommand(RIGHT_GROUP_NAME_ADMIN, RIGHTS_ADMIN),
            GENERIC_RIGHT_GROUP_COMMAND_ADMIN_FULL = new GenericRightGroupCommand(RIGHT_GROUP_NAME_ADMIN, RIGHTS_ADMIN_FULL),
            GENERIC_RIGHT_GROUP_COMMAND_OWNER = new GenericRightGroupCommand(RIGHT_GROUP_NAME_OWNER, RIGHTS_OWNER),
            GENERIC_RIGHT_GROUP_COMMAND_OWNER_FULL = new GenericRightGroupCommand(RIGHT_GROUP_NAME_OWNER, RIGHTS_OWNER_2),
            GENERIC_RIGHT_GROUP_COMMAND_NORMAL = new GenericRightGroupCommand(RIGHT_GROUP_NAME_OWNER, RIGHTS_NORMAL);

    private RightGroup rightGroupSu_create_1 = null,
            rightGroupSu_create_2 = null,
            rightGroupSu_update_1 = null,
            rightGroupSu_update_2 = null,
            rightGroupAdmin_Extra = null,
            rightGroupAdmin_Full = null,
            rightGroupOwner = null,
            rightGroupOwner_Full = null,
            rightGroupNormal = null;

    private final int PAGE_NUMBER = 0, PAGE_SIZE = 10;
    private final long TOTAL_COUNT = 6L;
    private final long TOTAL_COUNT_OWNER_1 = 2L;
    private final long TOTAL_COUNT_OWNER_2 = 1L;

    private final String USERNAME_SU = "janedoe",
            USERNAME_ADMIN = "peterpan",
            USERNAME_ADMIN_1 = "elem001",
            USERNAME_OWNER = "johndoe",
            USERNAME_OWNER_2 = "newman",
            USERNAME_OWNER_FULL = "jeandoe",
            USERNAME_NORMAL = "smithownens",
            USERNAME_NORMAL_1 = "thikatown";
    private final GenericUserCommand
            GENERIC_USER_COMMAND_SU = new GenericUserCommand(
            Role.SU,
            USERNAME_SU,
            "jane",
            "doe",
            "JANEDOE@mail.com",
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
            GENERIC_USER_COMMAND_ADMIN_1 = new GenericUserCommand(
                    Role.ADMIN,
                    USERNAME_ADMIN_1,
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
            GENERIC_USER_COMMAND_OWNER_FULL = new GenericUserCommand(
                    Role.OWNER,
                    USERNAME_OWNER_FULL,
                    "john",
                    "doe",
                    "janedoe@mail.com",
                    254_872_902_872L,
                    Status.ACTIVE,
                    List.of(RIGHT_GROUP_ID_5)),
            GENERIC_USER_COMMAND_OWNER_2 = new GenericUserCommand(
                    Role.OWNER,
                    USERNAME_OWNER_2,
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
                    Status.ACTIVE,
                    List.of(RIGHT_GROUP_ID_1, RIGHT_GROUP_ID_2));
    private User userSu = null,
            userAdmin = null,
            userAdmin_Full = null,
            userAdmin_EXT = null,
            userOwner = null,
            userOwner_Full = null,
            userOwner_2 = null,
            userNormal = null,
            userNormal_1 = null;
//            userNormal_Full = null;

    private final String SU_PASSWORD = "Nu8&^%$#opd7s",
            ADMIN_PASSWORD = "He7S9J@&H46#",
            OWNER_PASSWORD = "4gFP0M@F(K$SR",
            NORMAL_PASSWORD = "M8U#BF89SSh",
            NORMAL_1_PASSWORD = "Y5U#BF89sdDj";

    @BeforeAll
    void setup() {
        truncateAllTables();

        //right group
        rightGroupSu_create_1 = rightGroupDataMapper
                .transformGenericRightGroupCommandToRightGroup(EXECUTION_USER_SU_1,
                        GENERIC_RIGHT_GROUP_COMMAND_ADMIN,
                        new RightGroupId(RIGHT_GROUP_ID_1));
        rightGroupSu_create_2 = rightGroupDataMapper
                .transformGenericRightGroupCommandToRightGroup(EXECUTION_USER_SU_1,
                        GENERIC_RIGHT_GROUP_COMMAND_ADMIN,
                        new RightGroupId(RIGHT_GROUP_ID_SU));
        rightGroupAdmin_Extra = rightGroupDataMapper
                .transformGenericRightGroupCommandToRightGroup(EXECUTION_USER_ADMIN_1,
                        GENERIC_RIGHT_GROUP_COMMAND_ADMIN_FULL,
                        new RightGroupId(RIGHT_GROUP_ID_ADMIN_MIN));
        rightGroupAdmin_Full = rightGroupDataMapper
                .transformGenericRightGroupCommandToRightGroup(EXECUTION_USER_ADMIN,
                        GENERIC_RIGHT_GROUP_COMMAND_ADMIN_FULL,
                        new RightGroupId(RIGHT_GROUP_ID_3));
        rightGroupOwner = rightGroupDataMapper
                .transformGenericRightGroupCommandToRightGroup(EXECUTION_USER_OWNER,
                        GENERIC_RIGHT_GROUP_COMMAND_OWNER,
                        new RightGroupId(RIGHT_GROUP_ID_2));
        rightGroupOwner_Full = rightGroupDataMapper
                .transformGenericRightGroupCommandToRightGroup(EXECUTION_USER_OWNER,
                        GENERIC_RIGHT_GROUP_COMMAND_OWNER_FULL,
                        new RightGroupId(RIGHT_GROUP_ID_5));
        rightGroupNormal = rightGroupDataMapper
                .transformGenericRightGroupCommandToRightGroup(EXECUTION_USER_OWNER_2,
                        GENERIC_RIGHT_GROUP_COMMAND_NORMAL,
                        new RightGroupId(RIGHT_GROUP_ID_4));

        rightGroupSu_update_1 = rightGroupDataMapper
                .transformGenericRightGroupCommandToRightGroup(EXECUTION_USER_SU_1,
                        new GenericRightGroupCommand("kxy", List.of(Right.RIGHT_GROUP_CREATE, Right.USER_UPDATE)),
                        new RightGroupId(RIGHT_GROUP_ID_1));
        rightGroupSu_update_2 = rightGroupDataMapper
                .transformGenericRightGroupCommandToRightGroup(EXECUTION_USER_SU_1,
                        new GenericRightGroupCommand("mwenyeji", List.of(Right.RIGHT_GROUP_UPDATE, Right.USER_CREATE)),
                        new RightGroupId(RIGHT_GROUP_ID_SU));


        userSu = userDataMapper.transformGenericUserCommandToUser(GENERIC_USER_COMMAND_SU, new UserId(SU_ID_1),
                List.of(rightGroupSu_create_1), new Password(passwordEncoder.encode(SU_PASSWORD)), null);
        userAdmin = userDataMapper.transformGenericUserCommandToUser(GENERIC_USER_COMMAND_ADMIN, new UserId(ADMIN_ID),
                List.of(rightGroupSu_create_1), new Password(passwordEncoder.encode(ADMIN_PASSWORD)), null);
        userAdmin_Full = userDataMapper.transformGenericUserCommandToUser(GENERIC_USER_COMMAND_ADMIN_1, new UserId(ADMIN_ID),
                List.of(rightGroupAdmin_Full), new Password(passwordEncoder.encode(ADMIN_PASSWORD)), null);
        userAdmin_EXT = userDataMapper.transformGenericUserCommandToUser(GENERIC_USER_COMMAND_ADMIN_1, new UserId(ADMIN_ID_EXT),
                List.of(rightGroupAdmin_Full), new Password(passwordEncoder.encode(ADMIN_PASSWORD)), null);
        userOwner = userDataMapper.transformGenericUserCommandToUser(GENERIC_USER_COMMAND_OWNER, new UserId(OWNER_ID),
                List.of(rightGroupOwner), new Password(passwordEncoder.encode(OWNER_PASSWORD)), null);
        userOwner_Full = userDataMapper.transformGenericUserCommandToUser(GENERIC_USER_COMMAND_OWNER_FULL, new UserId(OWNER_ID_FULL),
                List.of(rightGroupOwner_Full), new Password(passwordEncoder.encode(OWNER_PASSWORD)), null);
        userOwner_2 = userDataMapper.transformGenericUserCommandToUser(GENERIC_USER_COMMAND_OWNER_2, new UserId(OWNER_ID_2),
                List.of(rightGroupOwner), new Password(passwordEncoder.encode(OWNER_PASSWORD)), null);
        userNormal = userDataMapper.transformGenericUserCommandToUser(GENERIC_USER_COMMAND_NORMAL, new UserId(NORMAL_ID),
                List.of(rightGroupSu_create_1, rightGroupOwner), new Password(passwordEncoder.encode(NORMAL_PASSWORD)), new OwnerId(OWNER_ID_1));
        userNormal_1 = userDataMapper.transformGenericUserCommandToUser(GENERIC_USER_COMMAND_NORMAL_1, new UserId(NORMAL_ID_1),
                List.of(rightGroupSu_create_1, rightGroupOwner), new Password(passwordEncoder.encode(NORMAL_1_PASSWORD)), new OwnerId(OWNER_ID_1));
//        userNormal_Full = userDataMapper.transformGenericUserCommandToUser(GENERIC_USER_COMMAND_NORMAL_1, new UserId(NORMAL_ID_Full),
//                List.of(rightGroupSu_create_1, rightGroupOwner), new Password(passwordEncoder.encode(NORMAL_1_PASSWORD)), new OwnerId(OWNER_ID_FULL));
    }

    private void truncateAllTables() {
        jdbcTemplate.execute("TRUNCATE TABLE right_groups CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE users CASCADE");
    }

    @Test
    @Order(0)
    void rightGroup_Create() {
        rightGroupRepository.create(rightGroupSu_create_1);
        rightGroupRepository.create(rightGroupSu_create_2);
        rightGroupRepository.create(rightGroupAdmin_Extra);
        rightGroupRepository.create(rightGroupAdmin_Full);
        rightGroupRepository.create(rightGroupOwner);
        rightGroupRepository.create(rightGroupOwner_Full);
        rightGroupRepository.create(rightGroupNormal);
    }

    @Test
    @Order(1)
    void rightGroup_Update_Success() throws InterruptedException {
        Thread.sleep(1000);
        rightGroupRepository.update(rightGroupSu_update_1);
        rightGroupRepository.update(rightGroupSu_update_2);
    }

    @Test
    @Order(2)
    void rightGroup_Update_Fail() {
        assertThrows(RecordUpdateFailed.class,
                () -> rightGroupRepository.update(rightGroupDataMapper
                        .transformGenericRightGroupCommandToRightGroup(EXECUTION_USER_SU_1,
                                new GenericRightGroupCommand("amlly",
                                        List.of(Right.RIGHT_GROUP_UPDATE, Right.USER_CREATE)),
                                new RightGroupId(UUID.fromString("da7b8bcc-c25d-4ba2-a13e-eaf8cc683ce1")))));
    }

    @Test
    @Order(3)
    void rightGroup_Delete() {
        rightGroupRepository.delete(rightGroupSu_create_1);
    }

    @Test
    @Order(4)
    void rightGroup_FindById_Fail() {
        var rightGroupOptional = rightGroupRepository.findById(rightGroupSu_create_1.getId());
        assertTrue(rightGroupOptional.isEmpty());
    }

    @Test
    @Order(5)
    void rightGroup_FindById_Success() {
        var rightGroupOptional = rightGroupRepository.findById(rightGroupSu_create_2.getId());
        assertFalse(rightGroupOptional.isEmpty());
    }

    @Test
    @Order(6)
    void rightGroup_FindByIds_Fail() {
        var rightGroupOptional = rightGroupRepository.findByIds(
                List.of(new RightGroupId(UUID.fromString("de534082-f103-438a-9db8-446a036cdcf4")),
                        new RightGroupId(UUID.fromString("4fa73f4b-fec5-488e-9092-895806f6894f")),
                        new RightGroupId(UUID.fromString("62afb103-ebdc-47c8-aad6-2db043daffa4")))
        );
        assertTrue(rightGroupOptional.isEmpty());
    }

    @Test
    @Order(7)
    void rightGroup_FindByIds_Success() {
        var rightGroupOptional_1 = rightGroupRepository.findByIds(
                List.of(rightGroupSu_create_1.getId(),
                        rightGroupSu_create_2.getId(),
                        rightGroupAdmin_Extra.getId()));
        assertFalse(rightGroupOptional_1.isEmpty());
        assertEquals(2, rightGroupOptional_1.get().size());

        var rightGroupOptional_2 = rightGroupRepository.findByIds(
                List.of(rightGroupSu_create_2.getId(),
                        rightGroupAdmin_Extra.getId(),
                        rightGroupOwner.getId()
                ));
        assertFalse(rightGroupOptional_2.isEmpty());
        assertEquals(3, rightGroupOptional_2.get().size());
    }

    @Test
    @Order(8)
    void rightGroup_FindAll_Fail() {
        var rightGroupOptional_1 = rightGroupRepository.findAll(2, PAGE_SIZE);
        assertTrue(rightGroupOptional_1.isEmpty());
    }

    @Test
    @Order(9)
    void rightGroup_FindAll_Success() {
        var rightGroupOptional_1 = rightGroupRepository.findAll(PAGE_NUMBER, PAGE_SIZE);
        assertFalse(rightGroupOptional_1.isEmpty());
        assertEquals(TOTAL_COUNT, rightGroupOptional_1.get().size());
    }

    @Test
    @Order(10)
    void rightGroup_CountAll_Success() {
        assertEquals(TOTAL_COUNT, rightGroupRepository.countAll());
    }

    @Test
    @Order(11)
    void rightGroup_FindAll_By_OwnerId_Fail() {
        var rightGroupOptional_1 = rightGroupRepository.findAll(new OwnerId(OWNER_ID_EXEC), 2, PAGE_SIZE);
        assertTrue(rightGroupOptional_1.isEmpty());

        var rightGroupOptional_2 = rightGroupRepository.findAll(new OwnerId(OWNER_ID_2), 2, PAGE_SIZE);
        assertTrue(rightGroupOptional_2.isEmpty());

        var rightGroupOptional_3 = rightGroupRepository.findAll(new OwnerId(OWNER_ID), PAGE_NUMBER, PAGE_SIZE);
        assertTrue(rightGroupOptional_3.isEmpty());
    }

    @Test
    @Order(12)
    void rightGroup_FindAll_By_OwnerId_Success() {
        var rightGroupOptional_1 = rightGroupRepository.findAll(new OwnerId(OWNER_ID_EXEC), PAGE_NUMBER, PAGE_SIZE);
        assertFalse(rightGroupOptional_1.isEmpty());
        assertEquals(TOTAL_COUNT_OWNER_1, rightGroupOptional_1.get().size());

        var rightGroupOptional_2 = rightGroupRepository.findAll(new OwnerId(OWNER_ID_2), PAGE_NUMBER, PAGE_SIZE);
        assertFalse(rightGroupOptional_2.isEmpty());
        assertEquals(TOTAL_COUNT_OWNER_2, rightGroupOptional_2.get().size());
    }

    @Test
    @Order(13)
    void rightGroup_CountAll_By_OwnerId_Success() {
        assertEquals(TOTAL_COUNT_OWNER_1, rightGroupRepository.countAll(new OwnerId(OWNER_ID_EXEC)));
        assertEquals(TOTAL_COUNT_OWNER_2, rightGroupRepository.countAll(new OwnerId(OWNER_ID_2)));
    }

    @Test
    @Order(14)
    void user_Create_Success() {
        userRepository.save(userSu);
        userRepository.save(userAdmin);
        userRepository.save(userAdmin_EXT);
        userRepository.save(userOwner_Full);
        userRepository.save(userOwner_2);
        userRepository.save(userNormal);
        userRepository.save(userNormal_1);
    }

    @Test
    @Order(15)
    void user_Create_Duplicate_Fail() {
        assertThrows(DuplicateRecord.class, () -> userRepository.save(userAdmin_Full));
    }

    @Test
    @Order(16)
    void user_Update_Success() throws InterruptedException {
        Thread.sleep(1000);
        userRepository.update(userDataMapper
                .transformGenericUserCommandToUser(new GenericUserCommand(
                                Role.ADMIN,
                                "unyama",
                                "peter",
                                "pan",
                                "peterpan@mail.com",
                                254_958_349_902L,
                                Status.ACTIVE,
                                List.of(RIGHT_GROUP_ID_1)),
                        new UserId(ADMIN_ID),
                        List.of(rightGroupSu_create_1),
                        new Password(passwordEncoder.encode(ADMIN_PASSWORD)), null));
    }

    @Test
    @Order(17)
    void user_Update_Fail() {
        assertThrows(RecordUpdateFailed.class, () -> userRepository.update(userDataMapper
                .transformGenericUserCommandToUser(new GenericUserCommand(
                                Role.ADMIN,
                                "unyama",
                                "peter",
                                "pan",
                                "peterpan@mail.com",
                                254_958_349_902L,
                                Status.ACTIVE,
                                List.of(RIGHT_GROUP_ID_1)),
                        new UserId(UUID.fromString("94480245-825a-4d04-98c4-c103e42d93f8")),
                        List.of(rightGroupSu_create_1),
                        new Password(passwordEncoder.encode(ADMIN_PASSWORD)), null)));
    }

    @Test
    @Order(18)
    void user_Update_Password_Success() throws InterruptedException {
        Thread.sleep(1000);
        userRepository.updatePassword(userDataMapper
                .transformGenericUserCommandToUser(new GenericUserCommand(
                                Role.ADMIN,
                                "unyama",
                                "peter",
                                "pan",
                                "peterpan@mail.com",
                                254_958_349_902L,
                                Status.ACTIVE,
                                List.of(RIGHT_GROUP_ID_1)),
                        new UserId(ADMIN_ID),
                        List.of(rightGroupSu_create_1),
                        new Password(passwordEncoder.encode(ADMIN_PASSWORD)), null));
    }

    @Test
    @Order(19)
    void user_Update_Password_Fail() {
        assertThrows(RecordUpdateFailed.class, () -> userRepository.updatePassword(userDataMapper
                .transformGenericUserCommandToUser(new GenericUserCommand(
                                Role.ADMIN,
                                "unyama",
                                "peter",
                                "pan",
                                "peterpan@mail.com",
                                254_958_349_902L,
                                Status.ACTIVE,
                                List.of(RIGHT_GROUP_ID_1)),
                        new UserId(UUID.fromString("94480245-825a-4d04-98c4-c103e42d93f8")),
                        List.of(rightGroupSu_create_1),
                        new Password(passwordEncoder.encode(ADMIN_PASSWORD)), null)));
    }

    @Test
    @Order(20)
    void user_Delete() {
        userRepository.delete(userAdmin);
    }

    @Test
    @Order(21)
    void user_Find_By_Id_Fail() {
        var userOptional = userRepository.findById(userAdmin.getId());
        assertTrue(userOptional.isEmpty());

        var userOptional_1 = userRepository.findById(new UserId(UUID.fromString("a93da6c4-4ea5-42a8-98c1-9b53ef322a8e")));
        assertTrue(userOptional_1.isEmpty());
    }

    @Test
    @Order(22)
    void user_Find_By_Id_Success() {
        var userOptional = userRepository.findById(userNormal.getId());
        assertFalse(userOptional.isEmpty());
        var user = userOptional.get();
        assertEquals(userNormal.getId(), user.getId());
        assertEquals(2, userNormal.getRightGroups().size());
    }

    @Test
    @Order(23)
    void user_Find_By_Username_Fail() {
        var userOptional = userRepository.findByUsername(userAdmin.getUsername());
        assertTrue(userOptional.isEmpty());

        var userOptional_1 = userRepository.findByUsername(new Username("civilian"));
        assertTrue(userOptional_1.isEmpty());
    }

    @Test
    @Order(24)
    void user_Find_By_Username_Success() {
        var userOptional = userRepository.findByUsername(userSu.getUsername());
        assertFalse(userOptional.isEmpty());
        var user = userOptional.get();
        assertEquals(userSu.getUsername(), user.getUsername());
    }

    @Test
    @Order(25)
    void user_Find_All_Users_Fail() {
        var usersOptional = userRepository.findAll(2, PAGE_SIZE);
        assertTrue(usersOptional.isEmpty());
    }

    @Test
    @Order(26)
    void user_Find_All_Users_Success() {
        var usersOptional = userRepository.findAll(PAGE_NUMBER, PAGE_SIZE);
        assertFalse(usersOptional.isEmpty());
        assertEquals(TOTAL_COUNT, usersOptional.get().size());
    }

    @Test
    @Order(27)
    void user_Count_All_Users_Success() {
        assertEquals(TOTAL_COUNT, userRepository.countAll());
    }

    @Test
    @Order(28)
    void user_Find_All_Users_By_UserType_Fail() {
        var usersOptional = userRepository.findAllByUserType(new UserType(Role.OWNER), 2, PAGE_SIZE);
        assertTrue(usersOptional.isEmpty());
    }

    @Test
    @Order(29)
    void user_Find_All_Users_By_UserType_Success() {
        var usersOptional_0 = userRepository.findAllByUserType(new UserType(Role.OWNER),PAGE_NUMBER, PAGE_SIZE);
        assertFalse(usersOptional_0.isEmpty());
        assertEquals(2, usersOptional_0.get().size());

        var usersOptional_1 = userRepository.findAllByUserType(new UserType(Role.ADMIN),PAGE_NUMBER, PAGE_SIZE);
        assertFalse(usersOptional_1.isEmpty());
        assertEquals(1, usersOptional_1.get().size());

        var usersOptional_3 = userRepository.findAllByUserType(new UserType(Role.NORMAL),PAGE_NUMBER, PAGE_SIZE);
        assertFalse(usersOptional_3.isEmpty());
        assertEquals(2, usersOptional_3.get().size());

        var usersOptional_4 = userRepository.findAllByUserType(new UserType(Role.SU),PAGE_NUMBER, PAGE_SIZE);
        assertFalse(usersOptional_4.isEmpty());
        assertEquals(1, usersOptional_4.get().size());
    }

    @Test
    @Order(30)
    void user_Count_All_Users_By_UserType_Success() {
        assertEquals(2, userRepository.countAllByUserType(new UserType(Role.OWNER)));
        assertEquals(1, userRepository.countAllByUserType(new UserType(Role.ADMIN)));
        assertEquals(2, userRepository.countAllByUserType(new UserType(Role.NORMAL)));
        assertEquals(1, userRepository.countAllByUserType(new UserType(Role.SU)));
    }

    @Test
    @Order(31)
    void user_Find_All_Users_By_OwnerId_Not_Existence_OwnerId_Fail() {
        var usersOptional = userRepository
                .findAllByOwnerId(new OwnerId(UUID.fromString("6df85196-e64e-4a3a-b1c2-ea16eea5f561")), PAGE_NUMBER, PAGE_SIZE);
        assertTrue(usersOptional.isEmpty());
    }

    @Test
    @Order(32)
    void user_Find_All_Users_By_OwnerId_Existing_Owner_Without_Users_Fail() {
        var usersOptional = userRepository
                .findAllByOwnerId(new OwnerId(OWNER_ID_2), PAGE_NUMBER, PAGE_SIZE);
        assertTrue(usersOptional.isEmpty());
    }

    @Test
    @Order(33)
    void user_Find_All_Users_By_OwnerId_Existing_Owner_But_Overflow_Page_Fail() {
        var usersOptional = userRepository
                .findAllByOwnerId(new OwnerId(OWNER_ID_1), 2, PAGE_SIZE);
        assertTrue(usersOptional.isEmpty());
    }

    @Test
    @Order(34)
    void user_Find_All_Users_By_OwnerId_Success() {
        var usersOptional_0 = userRepository.findAllByOwnerId(new OwnerId(OWNER_ID_1),PAGE_NUMBER, PAGE_SIZE);
        assertFalse(usersOptional_0.isEmpty());
        assertEquals(2, usersOptional_0.get().size());
    }

    @Test
    @Order(35)
    void user_Count_All_Users_By_OwnerId_Success() {
        assertEquals(2, userRepository.countAllByOwnerId(new OwnerId(OWNER_ID_1)));
    }

    @Test
    @Order(36)
    void user_Find_All_Users_By_SearchTerm_Invalid_SearchTerm_Fail() {
        var usersOptional = userRepository
                .findAllBySearchTerm("johnydepp", PAGE_NUMBER, PAGE_SIZE);
        assertTrue(usersOptional.isEmpty());
    }

    @Test
    @Order(37)
    void user_Find_All_Users_By_SearchTerm_Overflow_Page_Fail() {
        var usersOptional = userRepository
                .findAllBySearchTerm("SMITH", 2, PAGE_SIZE);
        assertTrue(usersOptional.isEmpty());
    }

    @Test
    @Order(38)
    void user_Find_All_Users_By_SearchTerm_Success() {
        var usersOptional_0 = userRepository.findAllBySearchTerm("SMITH",PAGE_NUMBER, PAGE_SIZE);
        assertFalse(usersOptional_0.isEmpty());
        assertEquals(1, usersOptional_0.get().size());

        var usersOptional_1 = userRepository.findAllBySearchTerm("DOE",PAGE_NUMBER, PAGE_SIZE);
        assertFalse(usersOptional_1.isEmpty());
        assertEquals(3, usersOptional_1.get().size());

        var usersOptional_2 = userRepository.findAllBySearchTerm("t",PAGE_NUMBER, PAGE_SIZE);
        assertFalse(usersOptional_2.isEmpty());
        assertEquals(3, usersOptional_2.get().size());

        var usersOptional_3 = userRepository.findAllBySearchTerm("JANE",PAGE_NUMBER, PAGE_SIZE);
        assertFalse(usersOptional_3.isEmpty());
        assertEquals(3, usersOptional_3.get().size());
    }

    @Test
    @Order(39)
    void user_Count_All_Users_By_SearchTerm_Success() {
        assertEquals(1, userRepository.countAllBySearchTerm("SMITH"));
        assertEquals(3, userRepository.countAllBySearchTerm("DOE"));
        assertEquals(3, userRepository.countAllBySearchTerm("t"));
        assertEquals(3, userRepository.countAllBySearchTerm("JANE"));
    }

    @Test
    @Order(40)
    void user_Find_All_Users_By_SearchTerm_And_OwnerId_Invalid_SearchTerm_Fail() {
        var usersOptional = userRepository
                .findAllByOwnerIdAndSearchTerm(new OwnerId(OWNER_ID_1),"johnydepp", PAGE_NUMBER, PAGE_SIZE);
        assertTrue(usersOptional.isEmpty());
    }

    @Test
    @Order(41)
    void user_Find_All_Users_By_SearchTerm_And_OwnerId_Invalid_OwnerId_Fail() {
        var usersOptional = userRepository
                .findAllByOwnerIdAndSearchTerm(new OwnerId(UUID.fromString("cb5128ff-eacf-42fd-877a-f42248a331a6")),"doe", PAGE_NUMBER, PAGE_SIZE);
        assertTrue(usersOptional.isEmpty());
    }

    @Test
    @Order(42)
    void user_Find_All_Users_By_SearchTerm_And_OwnerId_Overflow_Page_Fail() {
        var usersOptional = userRepository
                .findAllByOwnerIdAndSearchTerm(new OwnerId(OWNER_ID_1),"SMITH", 2, PAGE_SIZE);
        assertTrue(usersOptional.isEmpty());
    }

    @Test
    @Order(43)
    void user_Find_All_Users_By_SearchTerm_And_OwnerId_Success() {
        var usersOptional_0 = userRepository.findAllByOwnerIdAndSearchTerm(new OwnerId(OWNER_ID_1),"T",PAGE_NUMBER, PAGE_SIZE);
        assertFalse(usersOptional_0.isEmpty());
        assertEquals(2, usersOptional_0.get().size());

        var usersOptional_1 = userRepository.findAllByOwnerIdAndSearchTerm(new OwnerId(OWNER_ID_1),"SMIT",PAGE_NUMBER, PAGE_SIZE);
        assertFalse(usersOptional_1.isEmpty());
        assertEquals(1, usersOptional_1.get().size());

        var usersOptional_2 = userRepository.findAllByOwnerIdAndSearchTerm(new OwnerId(OWNER_ID_1),"town",PAGE_NUMBER, PAGE_SIZE);
        assertFalse(usersOptional_2.isEmpty());
        assertEquals(1, usersOptional_2.get().size());
    }

    @Test
    @Order(44)
    void user_Count_All_Users_By_SearchTerm_And_OwnerId_Success() {
        assertEquals(2, userRepository.countAllByOwnerIdAndSearchTerm(new OwnerId(OWNER_ID_1),"T"));
        assertEquals(1, userRepository.countAllByOwnerIdAndSearchTerm(new OwnerId(OWNER_ID_1),"SMIT"));
        assertEquals(1, userRepository.countAllByOwnerIdAndSearchTerm(new OwnerId(OWNER_ID_1),"town"));
    }

}
