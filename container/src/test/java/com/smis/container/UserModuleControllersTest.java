package com.smis.container;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smis.common.core.dto.ApiResponse;
import com.smis.common.core.registry.OwnerManagedRightsRegistry;
import com.smis.common.core.util.Right;
import com.smis.common.core.util.Role;
import com.smis.common.core.valueobject.ExecutionUser;
import com.smis.common.core.valueobject.UserId;
import com.smis.common.core.valueobject.UserType;
import com.smis.user.application.dto.RightListItem;
import com.smis.user.application.rest.AuthController;
import com.smis.user.domain.dto.rightgroup.GenericRightGroupCommand;
import com.smis.user.domain.dto.rightgroup.RightGroupListResponse;
import com.smis.user.domain.dto.rightgroup.RightGroupResponse;
import com.smis.user.domain.dto.user.*;
import com.smis.user.domain.entity.RightGroup;
import com.smis.user.domain.entity.User;
import com.smis.user.domain.mapper.RightGroupDataMapper;
import com.smis.user.domain.mapper.UserDataMapper;
import com.smis.user.domain.ports.output.repository.RightGroupRepository;
import com.smis.user.domain.ports.output.repository.UserRepository;
import com.smis.user.domain.util.Status;
import com.smis.user.domain.valueobject.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static com.smis.common.core.util.Helpers.ROLE_OWNER_DEFAULT_RIGHT_GROUP_ID;
import static com.smis.user.domain.util.UserDomainConstants.PASSWORD_PATTERN_ERROR_MESSAGE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@WebMvcTest(controllers = {AuthController.class})
@ContextConfiguration(classes = {ControllersTestConfiguration.class})
@EnableAutoConfiguration(exclude = R2dbcAutoConfiguration.class)
public class UserModuleControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RightGroupDataMapper rightGroupDataMapper;

    @Autowired
    private RightGroupRepository rightGroupRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserDataMapper userDataMapper;

    @Autowired
    OwnerManagedRightsRegistry ownerManagedRightsRegistry;


    private final static String EXPIRED_ACCESS_TOKEN = "Bearer eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzZWxmIiwic3ViIjoiamFuZWRvZSIsImV4cCI6MTczMDM2NzI0MywiaWF0IjoxNzMwMzYzNjQzLCJ1c2VyIjp7InVzZXJJZCI6IjkyOTFmNjkxLTMzNjUtNDkzMi1hMzdmLTIzNTJmNjk1MTI5OSIsInVzZXJUeXBlIjoiU1UiLCJmdWxsTmFtZXMiOiJqYW5lIGRvZSJ9LCJhdXRob3JpdGllcyI6WyJTVSIsIlVTRVJfREVMRVRFIiwiVVNFUl9DUkVBVEUiXX0.Te_a-lY2qKJiQPUHSJ2uJxfSBj33Hfn0evIyFCNvb60RBVdBOF1Iy-u3wQL4Y8m5egzEEUQYplBuj-ManBdQZ6Xq2vhQ4fgauqMHuTzKoVoS9Oj463cdi08zNQo7o7s_NIGIO1P43itx2KkHOW_2ioAU_CCC7H2rz1kZBaclW6TiETZxVSYXaRAlj7n0mzVStyENL5EVJsILSRSp2GS4lUX1kzeMkS10IWzY_hubhONn4_A0A3HfjJBwhEn0JW371FUv32zESVQixJ0pyUq3sLlSANiXDhTL_vDD6iA_ySammgxqdA7YqcwTGEM6xSyHmCjCxjVqbXjSGMtEkaxvvg";
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
            EXECUTION_USER_SU = new ExecutionUser(new UserId(SU_ID), new UserType(Role.SU)),
            EXECUTION_USER_SU_1 = new ExecutionUser(new UserId(SU_ID_1), new UserType(Role.SU)),
            EXECUTION_USER_ADMIN = new ExecutionUser(new UserId(ADMIN_ID_EXEC), new UserType(Role.ADMIN)),
            EXECUTION_USER_ADMIN_1 = new ExecutionUser(new UserId(ADMIN_ID), new UserType(Role.ADMIN)),
            EXECUTION_USER_OWNER = new ExecutionUser(new UserId(OWNER_ID_EXEC), new UserType(Role.OWNER)),
            EXECUTION_USER_OWNER_1 = new ExecutionUser(new UserId(OWNER_ID_1), new UserType(Role.OWNER)),
            EXECUTION_USER_OWNER_2 = new ExecutionUser(new UserId(OWNER_ID_2), new UserType(Role.OWNER)),
            EXECUTION_USER_OWNER_Full = new ExecutionUser(new UserId(OWNER_ID_FULL), new UserType(Role.OWNER)),
            EXECUTION_USER_NORMAL = new ExecutionUser(new UserId(NORMAL_ID_EXEC), new UserType(Role.NORMAL)),
            EXECUTION_USER_NORMAL_1 = new ExecutionUser(new UserId(NORMAL_ID_1), new UserType(Role.NORMAL));
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
    private final static RightGroupResponse
            RIGHT_GROUP_RESPONSE_ADMIN = new RightGroupResponse(RIGHT_GROUP_ID_1, RIGHT_GROUP_NAME_ADMIN, RIGHTS_ADMIN),
            RIGHT_GROUP_RESPONSE_OWNER = new RightGroupResponse(RIGHT_GROUP_ID_2, RIGHT_GROUP_NAME_OWNER, RIGHTS_OWNER);
    private RightGroup rightGroupSu_1 = null, rightGroupSu_2 = null, rightGroupAdmin = null, rightGroupAdmin_1 = null, rightGroupAdmin_Extra = null, rightGroupAdmin_Full = null, rightGroupOwner = null, rightGroupOwner_Full = null, rightGroupNormal = null;
    private final int PAGE_NUMBER = 0, PAGE_SIZE = 10;
    private final long TOTAL_COUNT = 2L;
    private final static String USERNAME_SU = "janedoe",
            USERNAME_ADMIN = "peterpan",
            USERNAME_ADMIN_1 = "elem001",
            USERNAME_OWNER = "johndoe",
            USERNAME_OWNER_2 = "newman",
            USERNAME_OWNER_FULL = "jeandoe",
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
            userNormal_1 = null,
            userNormal_Full = null;
    private final static Names names = new Names("john");
    private final static OwnerId ownerId = new OwnerId(OWNER_ID_1);
    private final static String SU_PASSWORD = "Nu8&^%$#opd7s",
            ADMIN_PASSWORD = "He7S9J@&H46#",
            OWNER_PASSWORD = "4gFP0M@F(K$SR",
            NORMAL_PASSWORD = "M8U#BF89SSh",
            NORMAL_1_PASSWORD = "Y5U#BF89sdDj";
    private final static String LOGIN_URL = "/auth/login",
            REGISTER_URL = "/auth/register",
            RIGHT_GROUP_RIGHTS_URL = "/right-group/rights",
            RIGHT_GROUP_BASE_URL = "/right-group",
            USERS_BASE_URL = "/users";
    private String accessTokenSu = null,
            accessTokenAdmin = null,
            accessTokenAdmin_Full = null,
            accessTokenOwner = null,
            accessTokenOwner_2 = null,
            accessTokenOwner_Full = null,
            accessTokenNormal = null;
    private List<Right> allRights = new ArrayList<>();
    private int totalRightsCount = 0, ownerManagedRightsCount = 0;
    private final PasswordModifyUserCommand PASSWORD_MODIFY_USER_COMMAND = new PasswordModifyUserCommand("Nu8&^%$#opd7s");

    @BeforeAll
    void setup() {
        totalRightsCount = Right.values().length;
        ownerManagedRightsCount = ownerManagedRightsRegistry.getOwnerManagedRights().size();

        //right group
        rightGroupSu_1 = rightGroupDataMapper.transformGenericRightGroupCommandToRightGroup(EXECUTION_USER_SU_1, GENERIC_RIGHT_GROUP_COMMAND_ADMIN, new RightGroupId(RIGHT_GROUP_ID_1));
        rightGroupSu_2 = rightGroupDataMapper.transformGenericRightGroupCommandToRightGroup(EXECUTION_USER_SU_1, GENERIC_RIGHT_GROUP_COMMAND_ADMIN, new RightGroupId(RIGHT_GROUP_ID_SU));
//        rightGroupAdmin = rightGroupDataMapper.transformGenericRightGroupCommandToRightGroup(EXECUTION_USER_ADMIN, GENERIC_RIGHT_GROUP_COMMAND_ADMIN, new RightGroupId(RIGHT_GROUP_ID_1));
        rightGroupAdmin = rightGroupDataMapper.transformGenericRightGroupCommandToRightGroup(EXECUTION_USER_ADMIN, GENERIC_RIGHT_GROUP_COMMAND_ADMIN, new RightGroupId(RIGHT_GROUP_ID_1));
        rightGroupAdmin_1 = rightGroupDataMapper.transformGenericRightGroupCommandToRightGroup(EXECUTION_USER_ADMIN_1, GENERIC_RIGHT_GROUP_COMMAND_ADMIN_FULL, new RightGroupId(RIGHT_GROUP_ID_3));
        rightGroupAdmin_Extra = rightGroupDataMapper.transformGenericRightGroupCommandToRightGroup(EXECUTION_USER_ADMIN_1, GENERIC_RIGHT_GROUP_COMMAND_ADMIN_FULL, new RightGroupId(RIGHT_GROUP_ID_ADMIN_MIN));
        rightGroupAdmin_Full = rightGroupDataMapper.transformGenericRightGroupCommandToRightGroup(EXECUTION_USER_ADMIN, GENERIC_RIGHT_GROUP_COMMAND_ADMIN_FULL, new RightGroupId(RIGHT_GROUP_ID_3));
        rightGroupOwner = rightGroupDataMapper.transformGenericRightGroupCommandToRightGroup(EXECUTION_USER_OWNER, GENERIC_RIGHT_GROUP_COMMAND_OWNER, new RightGroupId(RIGHT_GROUP_ID_2));
        rightGroupOwner_Full = rightGroupDataMapper.transformGenericRightGroupCommandToRightGroup(EXECUTION_USER_OWNER, GENERIC_RIGHT_GROUP_COMMAND_OWNER_FULL, new RightGroupId(RIGHT_GROUP_ID_5));
        rightGroupNormal = rightGroupDataMapper.transformGenericRightGroupCommandToRightGroup(EXECUTION_USER_OWNER_2, GENERIC_RIGHT_GROUP_COMMAND_NORMAL, new RightGroupId(RIGHT_GROUP_ID_4));

        when(rightGroupRepository.create(rightGroupSu_1)).thenReturn(rightGroupSu_1);
        when(rightGroupRepository.create(rightGroupAdmin)).thenReturn(rightGroupAdmin);
        when(rightGroupRepository.create(rightGroupAdmin_1)).thenReturn(rightGroupAdmin_1);
        when(rightGroupRepository.create(rightGroupOwner)).thenReturn(rightGroupOwner);
        when(rightGroupRepository.create(rightGroupNormal)).thenReturn(rightGroupNormal);
        when(rightGroupRepository.update(rightGroupAdmin_Full)).thenReturn(rightGroupAdmin_Full);
        when(rightGroupRepository.update(rightGroupSu_2)).thenReturn(rightGroupSu_2);
        when(rightGroupRepository.update(rightGroupAdmin)).thenReturn(rightGroupAdmin);
        when(rightGroupRepository.update(rightGroupAdmin_Extra)).thenReturn(rightGroupAdmin_Extra);
        when(rightGroupRepository.update(rightGroupOwner)).thenReturn(rightGroupOwner);
        when(rightGroupRepository.update(rightGroupNormal)).thenReturn(rightGroupNormal);
        when(rightGroupRepository.findById(new RightGroupId(RIGHT_GROUP_ID_SU))).thenReturn(Optional.of(rightGroupSu_2));
        when(rightGroupRepository.findById(new RightGroupId(RIGHT_GROUP_ID_1))).thenReturn(Optional.of(rightGroupAdmin));
        when(rightGroupRepository.findById(new RightGroupId(RIGHT_GROUP_ID_ADMIN_MIN))).thenReturn(Optional.of(rightGroupAdmin_Extra));
        when(rightGroupRepository.findById(new RightGroupId(RIGHT_GROUP_ID_2))).thenReturn(Optional.of(rightGroupOwner));
        when(rightGroupRepository.findById(new RightGroupId(RIGHT_GROUP_ID_3))).thenReturn(Optional.of(rightGroupAdmin_Full));
        when(rightGroupRepository.findById(new RightGroupId(RIGHT_GROUP_ID_4))).thenReturn(Optional.of(rightGroupNormal));
        when(rightGroupRepository.findById(new RightGroupId(RIGHT_GROUP_ID_5))).thenReturn(Optional.of(rightGroupOwner_Full));
        when(rightGroupRepository.findByIds(List.of(new RightGroupId(RIGHT_GROUP_ID_1), new RightGroupId(RIGHT_GROUP_ID_2))))
                .thenReturn(Optional.of(List.of(rightGroupAdmin, rightGroupOwner)));
        when(rightGroupRepository.findByIds(List.of(new RightGroupId(RIGHT_GROUP_ID_1))))
                .thenReturn(Optional.of(List.of(rightGroupAdmin)));
        when(rightGroupRepository.findByIds(List.of(new RightGroupId(RIGHT_GROUP_ID_2))))
                .thenReturn(Optional.of(List.of(rightGroupOwner)));

        when(rightGroupRepository.findAll(PAGE_NUMBER, PAGE_SIZE)).thenReturn(Optional.of(List.of(rightGroupAdmin, rightGroupOwner)));
        when(rightGroupRepository.countAll()).thenReturn(TOTAL_COUNT);

        when(rightGroupRepository.findAll(new OwnerId(OWNER_ID), PAGE_NUMBER, PAGE_SIZE))
                .thenReturn(Optional.of(List.of(rightGroupAdmin, rightGroupOwner)));
        when(rightGroupRepository.countAll(new OwnerId(OWNER_ID))).thenReturn(5L);

        //user
        userSu = userDataMapper.transformGenericUserCommandToUser(GENERIC_USER_COMMAND_SU, new UserId(SU_ID_1),
                List.of(rightGroupAdmin), new Password(passwordEncoder.encode(SU_PASSWORD)), null);
        userAdmin = userDataMapper.transformGenericUserCommandToUser(GENERIC_USER_COMMAND_ADMIN, new UserId(ADMIN_ID),
                List.of(rightGroupAdmin), new Password(passwordEncoder.encode(ADMIN_PASSWORD)), null);
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
                List.of(rightGroupAdmin, rightGroupOwner), new Password(passwordEncoder.encode(NORMAL_PASSWORD)), new OwnerId(OWNER_ID_1));
        userNormal_1 = userDataMapper.transformGenericUserCommandToUser(GENERIC_USER_COMMAND_NORMAL_1, new UserId(NORMAL_ID_1),
                List.of(rightGroupAdmin, rightGroupOwner), new Password(passwordEncoder.encode(NORMAL_1_PASSWORD)), new OwnerId(OWNER_ID_1));
        userNormal_Full = userDataMapper.transformGenericUserCommandToUser(GENERIC_USER_COMMAND_NORMAL_1, new UserId(NORMAL_ID_Full),
                List.of(rightGroupAdmin, rightGroupOwner), new Password(passwordEncoder.encode(NORMAL_1_PASSWORD)), new OwnerId(OWNER_ID_FULL));

        when(userRepository.save(argThat(user -> user != null && user.getUsername().equals(new Username(USERNAME_SU))))).thenReturn(userSu);
        when(userRepository.save(argThat(user -> user != null && user.getUsername().equals(new Username(USERNAME_ADMIN))))).thenReturn(userAdmin);
        when(userRepository.save(argThat(user -> user != null && user.getUsername().equals(new Username(USERNAME_OWNER))))).thenReturn(userOwner);
        when(userRepository.save(argThat(user -> user != null && user.getUsername().equals(new Username(USERNAME_NORMAL))))).thenReturn(userNormal);

        when(userRepository.update(argThat(user -> user != null && user.getId().equals(userSu.getId())))).thenReturn(userSu);
        when(userRepository.update(argThat(user -> user != null && user.getId().equals(userAdmin.getId())))).thenReturn(userAdmin);
        when(userRepository.update(argThat(user -> user != null && user.getId().equals(userAdmin_Full.getId())))).thenReturn(userAdmin_Full);
        when(userRepository.update(argThat(user -> user != null && user.getId().equals(userOwner.getId())))).thenReturn(userOwner);
        when(userRepository.update(argThat(user -> user != null && user.getId().equals(userOwner_Full.getId())))).thenReturn(userOwner_Full);
        when(userRepository.update(argThat(user -> user != null && user.getId().equals(userNormal.getId())))).thenReturn(userNormal);
        when(userRepository.update(argThat(user -> user != null && user.getId().equals(userNormal_Full.getId())))).thenReturn(userNormal_Full);

        when(userRepository.updatePassword(argThat(user -> user != null && user.getId().equals(userSu.getId())))).thenReturn(userSu);
        when(userRepository.updatePassword(argThat(user -> user != null && user.getId().equals(userAdmin.getId())))).thenReturn(userAdmin);
        when(userRepository.updatePassword(argThat(user -> user != null && user.getId().equals(userAdmin_Full.getId())))).thenReturn(userAdmin_Full);
        when(userRepository.updatePassword(argThat(user -> user != null && user.getId().equals(userOwner.getId())))).thenReturn(userOwner);
        when(userRepository.updatePassword(argThat(user -> user != null && user.getId().equals(userOwner_Full.getId())))).thenReturn(userOwner_Full);
        when(userRepository.updatePassword(argThat(user -> user != null && user.getId().equals(userNormal.getId())))).thenReturn(userNormal);
        when(userRepository.updatePassword(argThat(user -> user != null && user.getId().equals(userNormal_Full.getId())))).thenReturn(userNormal_Full);

        when(userRepository.findByUsername(new Username(USERNAME_SU))).thenReturn(Optional.of(userSu));
        when(userRepository.findByUsername(new Username(USERNAME_ADMIN))).thenReturn(Optional.of(userAdmin));
        when(userRepository.findByUsername(new Username(USERNAME_ADMIN_1))).thenReturn(Optional.of(userAdmin_Full));
        when(userRepository.findByUsername(new Username(USERNAME_OWNER))).thenReturn(Optional.of(userOwner));
        when(userRepository.findByUsername(new Username(USERNAME_OWNER_2))).thenReturn(Optional.of(userOwner_2));
        when(userRepository.findByUsername(new Username(USERNAME_OWNER_FULL))).thenReturn(Optional.of(userOwner_Full));
        when(userRepository.findByUsername(new Username(USERNAME_NORMAL))).thenReturn(Optional.of(userNormal));

        when(userRepository.findById(userSu.getId())).thenReturn(Optional.of(userSu));
        when(userRepository.findById(userAdmin.getId())).thenReturn(Optional.of(userAdmin));
        when(userRepository.findById(userAdmin_Full.getId())).thenReturn(Optional.of(userAdmin_Full));
        when(userRepository.findById(userAdmin_EXT.getId())).thenReturn(Optional.of(userAdmin_EXT));
        when(userRepository.findById(userOwner.getId())).thenReturn(Optional.of(userOwner));
        when(userRepository.findById(userOwner_2.getId())).thenReturn(Optional.of(userOwner_2));
        when(userRepository.findById(userOwner_Full.getId())).thenReturn(Optional.of(userOwner_Full));
        when(userRepository.findById(userNormal.getId())).thenReturn(Optional.of(userNormal));
        when(userRepository.findById(userNormal_Full.getId())).thenReturn(Optional.of(userNormal_Full));

        when(userRepository.findAll(PAGE_NUMBER, PAGE_SIZE)).thenReturn(Optional.of(List.of(userSu, userAdmin, userOwner)));
        when(userRepository.countAll()).thenReturn(4L);

        when(userRepository.findAllByUserType(new UserType(Role.SU), PAGE_NUMBER, PAGE_SIZE))
                .thenReturn(Optional.of(List.of(userSu)));
        when(userRepository.countAllByUserType(new UserType(Role.SU))).thenReturn(1L);

        when(userRepository.findAllByUserType(new UserType(Role.OWNER), PAGE_NUMBER, PAGE_SIZE))
                .thenReturn(Optional.of(List.of(userOwner)));
        when(userRepository.countAllByUserType(new UserType(Role.OWNER))).thenReturn(1L);

        when(userRepository.findAllByUserType(new UserType(Role.NORMAL), PAGE_NUMBER, PAGE_SIZE))
                .thenReturn(Optional.of(List.of(userNormal, userNormal_1)));
        when(userRepository.countAllByUserType(new UserType(Role.NORMAL))).thenReturn(2L);

        when(userRepository.findAllBySearchTerm(names.getValue(), PAGE_NUMBER, PAGE_SIZE))
                .thenReturn(Optional.of(List.of(userAdmin, userNormal)));
        when(userRepository.countAllBySearchTerm(names.getValue())).thenReturn(3L);

        when(userRepository.findAllByOwnerId(new OwnerId(OWNER_ID), PAGE_NUMBER, PAGE_SIZE))
                .thenReturn(Optional.of(List.of(userNormal, userNormal_1)));
        when(userRepository.countAllByOwnerId(new OwnerId(OWNER_ID))).thenReturn(7L);


        when(userRepository.findAllByOwnerId(new OwnerId(userOwner_Full.getId().getId()), PAGE_NUMBER, PAGE_SIZE))
                .thenReturn(Optional.of(List.of(userNormal, userNormal_1)));
        when(userRepository.countAllByOwnerId(new OwnerId(userOwner_Full.getId().getId()))).thenReturn(7L);

        when(userRepository.findAllByOwnerIdAndSearchTerm(new OwnerId(userOwner_Full.getId().getId()), names.getValue(), PAGE_NUMBER, PAGE_SIZE))
                .thenReturn(Optional.of(List.of(userNormal)));
        when(userRepository.countAllByOwnerIdAndSearchTerm(new OwnerId(userOwner_Full.getId().getId()), names.getValue())).thenReturn(5L);

        when(userRepository.findAllByOwnerIdAndSearchTerm(new OwnerId(OWNER_ID), names.getValue(), PAGE_NUMBER, PAGE_SIZE))
                .thenReturn(Optional.of(List.of(userNormal)));
        when(userRepository.countAllByOwnerIdAndSearchTerm(new OwnerId(OWNER_ID), names.getValue())).thenReturn(5L);

    }

    @Order(0)
    @Test
    void contextLoads() {
    }

    @Order(1)
    @Test
    void test_User_AuthController_login_Fail() throws Exception {
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    assertEquals("Unexpected error occurred!", apiResponse.message());
                });

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new LoginPayload("usrx", "pwdx"))))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    assertEquals("password " + PASSWORD_PATTERN_ERROR_MESSAGE, apiResponse.message());
                });

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new LoginPayload(null, "Nu9$h&s0#n*"))))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    assertNotNull(apiResponse.message());
                });

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new LoginPayload("xy7sfx", "Nu9$h&s0#n*"))))
                .andExpect(status().isUnauthorized())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    assertNotNull(apiResponse.message());
                });
    }

    @Order(2)
    @Test
    void test_User_AuthController_login_Success() throws Exception {
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new LoginPayload(USERNAME_SU, SU_PASSWORD))))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<LoginResponse> apiResponse = objectMapper
                            .readValue(jsonResponse, new TypeReference<>() {
                            });
                    log.info("{}", apiResponse);
                    accessTokenSu = "Bearer " + apiResponse.data().accessToken();
                    log.info("accessToken SU: {}", accessTokenSu);
                    assertNotNull(apiResponse.data().accessToken());
                });

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new LoginPayload(USERNAME_ADMIN, ADMIN_PASSWORD))))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<LoginResponse> apiResponse = objectMapper
                            .readValue(jsonResponse, new TypeReference<>() {
                            });
                    log.info("{}", apiResponse);
                    accessTokenAdmin = "Bearer " + apiResponse.data().accessToken();
                    log.info("accessToken ADMIN: {}", accessTokenAdmin);
                    assertNotNull(apiResponse.data().accessToken());
                });

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new LoginPayload(USERNAME_ADMIN_1, ADMIN_PASSWORD))))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<LoginResponse> apiResponse = objectMapper
                            .readValue(jsonResponse, new TypeReference<>() {
                            });
                    log.info("{}", apiResponse);
                    accessTokenAdmin_Full = "Bearer " + apiResponse.data().accessToken();
                    log.info("accessToken ADMIN_Full: {}", accessTokenAdmin_Full);
                    assertNotNull(apiResponse.data().accessToken());
                });

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new LoginPayload(USERNAME_OWNER, OWNER_PASSWORD))))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<LoginResponse> apiResponse = objectMapper
                            .readValue(jsonResponse, new TypeReference<>() {
                            });
                    log.info("{}", apiResponse);
                    accessTokenOwner = "Bearer " + apiResponse.data().accessToken();
                    log.info("accessToken OWNER: {}", accessTokenOwner);
                    assertNotNull(apiResponse.data().accessToken());
                });

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new LoginPayload(USERNAME_OWNER_2, OWNER_PASSWORD))))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<LoginResponse> apiResponse = objectMapper
                            .readValue(jsonResponse, new TypeReference<>() {
                            });
                    log.info("{}", apiResponse);
                    accessTokenOwner_2 = "Bearer " + apiResponse.data().accessToken();
                    log.info("accessToken OWNER 2: {}", accessTokenOwner);
                    assertNotNull(apiResponse.data().accessToken());
                });

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new LoginPayload(USERNAME_OWNER_FULL, OWNER_PASSWORD))))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<LoginResponse> apiResponse = objectMapper
                            .readValue(jsonResponse, new TypeReference<>() {
                            });
                    log.info("{}", apiResponse);
                    accessTokenOwner_Full = "Bearer " + apiResponse.data().accessToken();
                    log.info("accessToken OWNER 2: {}", accessTokenOwner_Full);
                    assertNotNull(apiResponse.data().accessToken());
                });

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new LoginPayload(USERNAME_NORMAL, NORMAL_PASSWORD))))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<LoginResponse> apiResponse = objectMapper
                            .readValue(jsonResponse, new TypeReference<>() {
                            });
                    log.info("{}", apiResponse);
                    accessTokenNormal = "Bearer " + apiResponse.data().accessToken();
                    log.info("accessToken NORMAL: {}", accessTokenNormal);
                    assertNotNull(apiResponse.data().accessToken());
                });
    }

    @Order(3)
    @Test
    void test_User_AuthController_register_Fail() throws Exception {
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    assertEquals("Unexpected error occurred!", apiResponse.message());
                });

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new GenericUserCommand(
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        0,
                                        null,
                                        null
                                ))))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    assertNotNull(apiResponse.message());
                });

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new GenericUserCommand(
                                        null,
                                        null,
                                        "jane",
                                        "doe",
                                        "janedoe@mail.com",
                                        254_902_456_781L,
                                        null,
                                        null
                                ))))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    assertNotNull(apiResponse.message());
                });

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new GenericUserCommand(
                                        null,
                                        "janedoe",
                                        null,
                                        "doe",
                                        "janedoe@mail.com",
                                        254_902_456_781L,
                                        null,
                                        null
                                ))))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    assertNotNull(apiResponse.message());
                });

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new GenericUserCommand(
                                        null,
                                        "janedoe",
                                        "jane",
                                        null,
                                        "janedoe@mail.com",
                                        254_902_456_781L,
                                        null,
                                        null
                                ))))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    assertNotNull(apiResponse.message());
                });

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new GenericUserCommand(
                                        null,
                                        "janedoe",
                                        "jane",
                                        "  ",
                                        "janedoe@mail.com",
                                        254_902_456_781L,
                                        null,
                                        null
                                ))))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    assertNotNull(apiResponse.message());
                });

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new GenericUserCommand(
                                        null,
                                        "janedoe",
                                        "jane",
                                        "doe",
                                        null,
                                        254_902_456_781L,
                                        null,
                                        null
                                ))))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    assertNotNull(apiResponse.message());
                });

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new GenericUserCommand(
                                        null,
                                        "janedoe",
                                        "jane",
                                        "doe",
                                        "jinah",
                                        254_902_456_781L,
                                        null,
                                        null
                                ))))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    assertNotNull(apiResponse.message());
                });

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new GenericUserCommand(
                                        null,
                                        "janedoe",
                                        "jane",
                                        "doe",
                                        "janedoe@mail.com",
                                        254_902_456L,
                                        null,
                                        null
                                ))))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    assertNotNull(apiResponse.message());
                });
    }

    @Order(4)
    @Test
    void test_User_AuthController_register_Success() throws Exception {
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new GenericUserCommand(
                                        null,
                                        "janedoe",
                                        "jane",
                                        "doe",
                                        "janedoe@mail.com",
                                        254_902_456_892L,
                                        null,
                                        null
                                ))))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    assertNotNull(apiResponse.message());
                });
    }

    @Order(5)
    @Test
    void test_User_RightGroupController_Get_Rights_AccessToken_Missing_Fail() throws Exception {
        //no accessToken
        mockMvc.perform(get(RIGHT_GROUP_RIGHTS_URL))
                .andExpect(status().isUnauthorized());
    }

    @Order(6)
    @Test
    void test_User_RightGroupController_Get_Rights_AccessToken_Expired_Fail() throws Exception {
        //expired accessToken
        mockMvc.perform(get(RIGHT_GROUP_RIGHTS_URL)
                        .header("Authorization", EXPIRED_ACCESS_TOKEN))
                .andExpect(status().isUnauthorized());
    }

    @Order(7)
    @Test
    void test_User_RightGroupController_Get_Rights_AccessDenied_For_NormalUser_Fail() throws Exception {
        //NORMAL not allowed
        mockMvc.perform(get(RIGHT_GROUP_RIGHTS_URL)
                        .header("Authorization", accessTokenNormal))
                .andExpect(status().isForbidden());
    }

    @Order(8)
    @Test
    void test_User_RightGroupController_Get_Rights_As_SU_Success() throws Exception {
        mockMvc.perform(get(RIGHT_GROUP_RIGHTS_URL)
                        .header("Authorization", accessTokenSu))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<List<RightListItem>> rightListItems = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    assertNotNull(rightListItems.data());
                    assertFalse(rightListItems.data().isEmpty());
                    assertEquals(totalRightsCount, rightListItems.data().size());
                });
    }

    @Order(9)
    @Test
    void test_User_RightGroupController_Get_Rights_As_ADMIN_Success() throws Exception {
        mockMvc.perform(get(RIGHT_GROUP_RIGHTS_URL)
                        .header("Authorization", accessTokenAdmin))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<List<RightListItem>> rightListItems = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    assertNotNull(rightListItems.data());
                    assertFalse(rightListItems.data().isEmpty());
                    assertEquals(totalRightsCount, rightListItems.data().size());
                    allRights.addAll(rightListItems.data().stream()
                            .map(RightListItem::right)
                            .toList());
                });
    }

    @Order(10)
    @Test
    void test_User_RightGroupController_Get_Rights_As_OWNER_Success() throws Exception {
        mockMvc.perform(get(RIGHT_GROUP_RIGHTS_URL)
                        .header("Authorization", accessTokenOwner))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<List<RightListItem>> rightListItems = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    assertNotNull(rightListItems.data());
                    assertFalse(rightListItems.data().isEmpty());
                    assertEquals(ownerManagedRightsCount, rightListItems.data().size());
                });
    }

    @Order(11)
    @Test
    void test_User_RightGroupController_FetchRightGroups_As_SU_Success() throws Exception {
        mockMvc.perform(get(RIGHT_GROUP_BASE_URL)
                        .header("Authorization", accessTokenSu)
                        .param("pageNumber", String.valueOf(PAGE_NUMBER))
                        .param("pageSize", String.valueOf(PAGE_SIZE)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<RightGroupListResponse> rightGroupListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    log.info("{}", rightGroupListResponse);
                    assertNotNull(rightGroupListResponse.data());
                    assertFalse(rightGroupListResponse.data().rightGroups().isEmpty());
                });
    }

    @Order(12)
    @Test
    void test_User_RightGroupController_FetchRightGroups_As_SU_OutOfBounds_Param_Fail() throws Exception {
        mockMvc.perform(get(RIGHT_GROUP_BASE_URL)
                        .header("Authorization", accessTokenSu)
                        .param("pageNumber", String.valueOf(12))
                        .param("pageSize", String.valueOf(PAGE_SIZE)))
                .andExpect(status().isNotFound())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    log.info("{}", apiResponse);
                    assertNotNull(apiResponse.message());
                });
    }

    @Order(13)
    @Test
    void test_User_RightGroupController_FetchRightGroups_As_ADMIN_Success() throws Exception {
        mockMvc.perform(get(RIGHT_GROUP_BASE_URL)
                        .header("Authorization", accessTokenAdmin_Full)
                        .param("pageNumber", String.valueOf(PAGE_NUMBER))
                        .param("pageSize", String.valueOf(PAGE_SIZE)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<RightGroupListResponse> rightGroupListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    log.info("{}", rightGroupListResponse);
                    assertNotNull(rightGroupListResponse.data());
                    assertFalse(rightGroupListResponse.data().rightGroups().isEmpty());
                });
    }

    @Order(14)
    @Test
    void test_User_RightGroupController_FetchRightGroups_As_ADMIN_OutOfBounds_Param_Fail() throws Exception {
        mockMvc.perform(get(RIGHT_GROUP_BASE_URL)
                        .header("Authorization", accessTokenAdmin_Full)
                        .param("pageNumber", String.valueOf(PAGE_NUMBER))
                        .param("pageSize", String.valueOf(100)))
                .andExpect(status().isNotFound())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    log.info("{}", apiResponse);
                    assertNotNull(apiResponse.message());
                });
    }

    @Order(15)
    @Test
    void test_User_RightGroupController_FetchRightGroups_As_ADMIN_Invalid_Param_Fail() throws Exception {
        mockMvc.perform(get(RIGHT_GROUP_BASE_URL)
                        .header("Authorization", accessTokenAdmin_Full)
                        .param("pageNumber", String.valueOf(PAGE_NUMBER))
                        .param("pageSize", String.valueOf(-1)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    log.info("{}", apiResponse);
                    assertNotNull(apiResponse.message());
                });
    }

    @Order(16)
    @Test
    void test_User_RightGroupController_FetchRightGroups_As_ADMIN_InvalidAdminRight_Fail() throws Exception {
        mockMvc.perform(get(RIGHT_GROUP_BASE_URL)
                        .header("Authorization", accessTokenAdmin)
                        .param("pageNumber", String.valueOf(PAGE_NUMBER))
                        .param("pageSize", String.valueOf(PAGE_SIZE)))
                .andExpect(status().isForbidden())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    log.info("{}", apiResponse);
                    assertNotNull(apiResponse.message());
                });
    }

    @Order(17)
    @Test
    void test_User_RightGroupController_FetchRightGroups_As_OWNER_Success() throws Exception {
        mockMvc.perform(get(RIGHT_GROUP_BASE_URL)
                        .header("Authorization", accessTokenOwner)
                        .param("pageNumber", String.valueOf(PAGE_NUMBER))
                        .param("pageSize", String.valueOf(PAGE_SIZE)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<RightGroupListResponse> rightGroupListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    log.info("{}", rightGroupListResponse);
                    assertNotNull(rightGroupListResponse.data());
                    assertFalse(rightGroupListResponse.data().rightGroups().isEmpty());
                });
    }

    @Order(18)
    @Test
    void test_User_RightGroupController_FetchRightGroups_As_OWNER_NotFound_Fail() throws Exception {
        mockMvc.perform(get(RIGHT_GROUP_BASE_URL)
                        .header("Authorization", accessTokenOwner_2)
                        .param("pageNumber", String.valueOf(PAGE_NUMBER))
                        .param("pageSize", String.valueOf(PAGE_SIZE)))
                .andExpect(status().isNotFound())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    assertNotNull(apiResponse.message());
                });
    }

    @Order(19)
    @Test
    void test_User_RightGroupController_FetchRightGroups_As_NORMAL_Unauthorized_Fail() throws Exception {
        mockMvc.perform(get(RIGHT_GROUP_BASE_URL)
                        .header("Authorization", accessTokenNormal)
                        .param("pageNumber", String.valueOf(PAGE_NUMBER))
                        .param("pageSize", String.valueOf(PAGE_SIZE)))
                .andExpect(status().isForbidden())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    assertNotNull(apiResponse.message());
                });
    }

    @Order(20)
    @Test
    void test_User_RightGroupController_CreateRightGroup_As_SU_Success() throws Exception {
        mockMvc.perform(post(RIGHT_GROUP_BASE_URL)
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_ADMIN)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<RightGroupResponse> rightGroupResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    log.info("{}", rightGroupResponse);
                    assertNotNull(rightGroupResponse.data());
                });
    }

    @Order(21)
    @Test
    void test_User_RightGroupController_CreateRightGroup_As_SU_InvalidPayload_Fail() throws Exception {
        mockMvc.perform(post(RIGHT_GROUP_BASE_URL)
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new GenericRightGroupCommand(
                                        null,
                                        null
                                ))))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    log.info("{}", apiResponse);
                    assertNotNull(apiResponse.message());
                });

        mockMvc.perform(post(RIGHT_GROUP_BASE_URL)
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new GenericRightGroupCommand(
                                        "group 001",
                                        null
                                ))))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    log.info("{}", apiResponse);
                    assertNotNull(apiResponse.message());
                });

        mockMvc.perform(post(RIGHT_GROUP_BASE_URL)
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new GenericRightGroupCommand(
                                        "group 001",
                                        new ArrayList<>()
                                ))))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    log.info("{}", apiResponse);
                    assertNotNull(apiResponse.message());
                });
    }

    @Order(22)
    @Test
    void test_User_RightGroupController_CreateRightGroup_As_Admin_Success() throws Exception {
        mockMvc.perform(post(RIGHT_GROUP_BASE_URL)
                        .header("Authorization", accessTokenAdmin_Full)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_ADMIN_FULL)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<RightGroupResponse> rightGroupResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    log.info("{}", rightGroupResponse);
                    assertNotNull(rightGroupResponse.data());
                });
    }

    @Order(23)
    @Test
    void test_User_RightGroupController_CreateRightGroup_As_Admin_InvalidAdminRight_Fail() throws Exception {
        mockMvc.perform(post(RIGHT_GROUP_BASE_URL)
                        .header("Authorization", accessTokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_ADMIN_FULL)))
                .andExpect(status().isForbidden());
    }

    @Order(24)
    @Test
    void test_User_RightGroupController_CreateRightGroup_As_Owner_Success() throws Exception {
        log.warn("data: {}", GENERIC_RIGHT_GROUP_COMMAND_NORMAL);
        mockMvc.perform(post(RIGHT_GROUP_BASE_URL)
                        .header("Authorization", accessTokenOwner_2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_NORMAL)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<RightGroupResponse> rightGroupResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    log.info("{}", rightGroupResponse);
                    assertNotNull(rightGroupResponse.data());
                });
    }

    @Order(25)
    @Test
    void test_User_RightGroupController_CreateRightGroup_As_Owner_Invalid_GroupRights_Fail() throws Exception {
        mockMvc.perform(post(RIGHT_GROUP_BASE_URL)
                        .header("Authorization", accessTokenOwner)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_ADMIN_FULL)))
                .andExpect(status().isBadRequest());
    }

    @Order(26)
    @Test
    void test_User_RightGroupController_UpdateRightGroup_As_SU_InvalidPayload_Fail() throws Exception {
        mockMvc.perform(patch(RIGHT_GROUP_BASE_URL + "/" + RIGHT_GROUP_ID_1)
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new GenericRightGroupCommand(
                                        null,
                                        null
                                ))))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    log.info("{}", apiResponse);
                    assertNotNull(apiResponse.message());
                });

        mockMvc.perform(patch(RIGHT_GROUP_BASE_URL + "/" + RIGHT_GROUP_ID_1)
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new GenericRightGroupCommand(
                                        "group 001",
                                        null
                                ))))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    log.info("{}", apiResponse);
                    assertNotNull(apiResponse.message());
                });

        mockMvc.perform(patch(RIGHT_GROUP_BASE_URL + "/" + RIGHT_GROUP_ID_1)
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new GenericRightGroupCommand(
                                        "group 001",
                                        new ArrayList<>()
                                ))))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    log.info("{}", apiResponse);
                    assertNotNull(apiResponse.message());
                });
    }

    @Order(26)
    @Test
    void test_User_RightGroupController_UpdateRightGroup_As_SU_For_OWNER_Fail() throws Exception {
        mockMvc.perform(patch(RIGHT_GROUP_BASE_URL + "/" + RIGHT_GROUP_ID_4)
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new GenericRightGroupCommand(
                                        null,
                                        null
                                ))))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    log.info("{}", apiResponse);
                    assertNotNull(apiResponse.message());
                });
    }

    @Order(27)
    @Test
    void test_User_RightGroupController_UpdateRightGroup_For_All_Roles_As_SU_Success() throws Exception {
        mockMvc.perform(patch(RIGHT_GROUP_BASE_URL + "/" + RIGHT_GROUP_ID_1)
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_ADMIN)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<RightGroupResponse> rightGroupResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    log.info("{}", rightGroupResponse);
                    assertNotNull(rightGroupResponse.data());
                });

        mockMvc.perform(patch(RIGHT_GROUP_BASE_URL + "/" + RIGHT_GROUP_ID_2)
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_OWNER)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<RightGroupResponse> rightGroupResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    log.info("{}", rightGroupResponse);
                    assertNotNull(rightGroupResponse.data());
                });
    }

    @Order(28)
    @Test
    void test_User_RightGroupController_UpdateRightGroup_As_Admin_Fail_For_Groups_Belonging_To_SU_And_OWNER() throws Exception {

        mockMvc.perform(patch(RIGHT_GROUP_BASE_URL + "/" + RIGHT_GROUP_ID_SU)
                        .header("Authorization", accessTokenAdmin_Full)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_OWNER)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    log.info("{}", apiResponse);
                    assertNotNull(apiResponse.message());
                });

        mockMvc.perform(patch(RIGHT_GROUP_BASE_URL + "/" + RIGHT_GROUP_ID_2)
                        .header("Authorization", accessTokenAdmin_Full)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_OWNER)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    log.info("{}", apiResponse);
                    assertNotNull(apiResponse.message());
                });
    }

    @Order(29)
    @Test
    void test_User_RightGroupController_UpdateRightGroup_As_Admin_Success_For_Groups_Belonging_To_Them_And_Other_SU() throws Exception {

        mockMvc.perform(patch(RIGHT_GROUP_BASE_URL + "/" + RIGHT_GROUP_ID_3)
                        .header("Authorization", accessTokenAdmin_Full)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_ADMIN_FULL)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<RightGroupResponse> rightGroupResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    log.info("{}", rightGroupResponse);
                    assertNotNull(rightGroupResponse.data());
                });

        mockMvc.perform(patch(RIGHT_GROUP_BASE_URL + "/" + RIGHT_GROUP_ID_ADMIN_MIN)
                        .header("Authorization", accessTokenAdmin_Full)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_ADMIN_FULL)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<RightGroupResponse> rightGroupResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    log.info("{}", rightGroupResponse);
                    assertNotNull(rightGroupResponse.data());
                });
    }

    @Order(30)
    @Test
    void test_User_RightGroupController_UpdateRightGroup_As_Owner_Fail_For_Groups_Belonging_To_SU_ADMIN_Other_OWNERS() throws Exception {

        mockMvc.perform(patch(RIGHT_GROUP_BASE_URL + "/" + RIGHT_GROUP_ID_SU)
                        .header("Authorization", accessTokenOwner)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_OWNER)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    log.info("{}", apiResponse);
                    assertNotNull(apiResponse.message());
                });

        mockMvc.perform(patch(RIGHT_GROUP_BASE_URL + "/" + RIGHT_GROUP_ID_3)
                        .header("Authorization", accessTokenOwner)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_OWNER)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    log.info("{}", apiResponse);
                    assertNotNull(apiResponse.message());
                });

        mockMvc.perform(patch(RIGHT_GROUP_BASE_URL + "/" + RIGHT_GROUP_ID_4)
                        .header("Authorization", accessTokenOwner)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_OWNER)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    log.info("{}", apiResponse);
                    assertNotNull(apiResponse.message());
                });
    }

    @Order(31)
    @Test
    void test_User_RightGroupController_UpdateRightGroup_As_Owner_Success_For_Groups_Belonging_To_Them() throws Exception {
        mockMvc.perform(patch(RIGHT_GROUP_BASE_URL + "/" + RIGHT_GROUP_ID_4)
                        .header("Authorization", accessTokenOwner_2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_NORMAL)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<RightGroupResponse> rightGroupResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    log.info("{}", rightGroupResponse);
                    assertNotNull(rightGroupResponse.data());
                });
    }


    @Order(32)
    @Test
    void test_User_RightGroupController_DeleteRightGroup_As_SU_Success() throws Exception {
        mockMvc.perform(delete(RIGHT_GROUP_BASE_URL + "/" + RIGHT_GROUP_ID_SU)
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_ADMIN)))
                .andExpect(status().isOk());

        mockMvc.perform(delete(RIGHT_GROUP_BASE_URL + "/" + RIGHT_GROUP_ID_1)
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_ADMIN)))
                .andExpect(status().isOk());

        mockMvc.perform(delete(RIGHT_GROUP_BASE_URL + "/" + RIGHT_GROUP_ID_2)
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_OWNER)))
                .andExpect(status().isOk());

        mockMvc.perform(delete(RIGHT_GROUP_BASE_URL + "/" + RIGHT_GROUP_ID_4)
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new GenericRightGroupCommand(
                                        null,
                                        null
                                ))))
                .andExpect(status().isOk());
    }

    @Order(33)
    @Test
    void test_User_RightGroupController_DeleteRightGroup_As_Admin_Fail() throws Exception {
        mockMvc.perform(delete(RIGHT_GROUP_BASE_URL + "/" + RIGHT_GROUP_ID_SU)
                        .header("Authorization", accessTokenAdmin_Full)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_ADMIN)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    log.info("{}", apiResponse);
                    assertNotNull(apiResponse.message());
                });

        mockMvc.perform(delete(RIGHT_GROUP_BASE_URL + "/" + RIGHT_GROUP_ID_4)
                        .header("Authorization", accessTokenAdmin_Full)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new GenericRightGroupCommand(
                                        null,
                                        null
                                ))))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    log.info("{}", apiResponse);
                    assertNotNull(apiResponse.message());
                });
    }

    @Order(34)
    @Test
    void test_User_RightGroupController_DeleteRightGroup_As_Admin_Success() throws Exception {
        mockMvc.perform(delete(RIGHT_GROUP_BASE_URL + "/" + RIGHT_GROUP_ID_1)
                        .header("Authorization", accessTokenAdmin_Full)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_ADMIN)))
                .andExpect(status().isOk());

        mockMvc.perform(delete(RIGHT_GROUP_BASE_URL + "/" + RIGHT_GROUP_ID_ADMIN_MIN)
                        .header("Authorization", accessTokenAdmin_Full)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new GenericRightGroupCommand(
                                        null,
                                        null
                                ))))
                .andExpect(status().isOk());
    }

    @Order(35)
    @Test
    void test_User_RightGroupController_DeleteRightGroup_As_Owner_Fail() throws Exception {
        mockMvc.perform(delete(RIGHT_GROUP_BASE_URL + "/" + RIGHT_GROUP_ID_SU)
                        .header("Authorization", accessTokenOwner)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_ADMIN)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    log.info("{}", apiResponse);
                    assertNotNull(apiResponse.message());
                });

        mockMvc.perform(delete(RIGHT_GROUP_BASE_URL + "/" + RIGHT_GROUP_ID_1)
                        .header("Authorization", accessTokenOwner)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_ADMIN)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    log.info("{}", apiResponse);
                    assertNotNull(apiResponse.message());
                });
        mockMvc.perform(delete(RIGHT_GROUP_BASE_URL + "/" + RIGHT_GROUP_ID_2)
                        .header("Authorization", accessTokenOwner)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_OWNER)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    log.info("{}", apiResponse);
                    assertNotNull(apiResponse.message());
                });

        mockMvc.perform(delete(RIGHT_GROUP_BASE_URL + "/" + RIGHT_GROUP_ID_4)
                        .header("Authorization", accessTokenOwner)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new GenericRightGroupCommand(
                                        null,
                                        null
                                ))))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    log.info("{}", apiResponse);
                    assertNotNull(apiResponse.message());
                });
    }

    @Order(36)
    @Test
    void test_User_RightGroupController_DeleteRightGroup_As_Owner_Success() throws Exception {
        mockMvc.perform(delete(RIGHT_GROUP_BASE_URL + "/" + RIGHT_GROUP_ID_4)
                        .header("Authorization", accessTokenOwner_2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_ADMIN)))
                .andExpect(status().isOk());
    }

    @Order(37)
    @Test
    void test_User_Fetch_As_SU_Invalid_RequestParams_Fail() throws Exception {
        mockMvc.perform(get(USERS_BASE_URL)
                        .header("Authorization", accessTokenOwner_2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_ADMIN)))
                .andExpect(status().isInternalServerError());
    }

    @Order(38)
    @Test
    void test_User_Fetch_As_SU_Success() throws Exception {
        mockMvc.perform(get(String.format("%s?pageNumber=%s&pageSize=%s", USERS_BASE_URL, PAGE_NUMBER, PAGE_SIZE))
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_ADMIN)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<UserListResponse> userListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    log.info("{}", userListResponse);
                    assertNotNull(userListResponse.data());
                });

        mockMvc.perform(get(String.format("%s?pageNumber=%s&pageSize=%s&names=%s", USERS_BASE_URL, PAGE_NUMBER, PAGE_SIZE, names.getValue()))
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_ADMIN)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<UserListResponse> userListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    log.info("{}", userListResponse);
                    assertNotNull(userListResponse.data());
                });
        mockMvc.perform(get(String.format("%s?pageNumber=%s&pageSize=%s&userType=%s", USERS_BASE_URL, PAGE_NUMBER, PAGE_SIZE, Role.NORMAL))
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_ADMIN)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<UserListResponse> userListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    log.info("{}", userListResponse);
                    assertNotNull(userListResponse.data());
                });
    }

    @Order(39)
    @Test
    void test_User_Fetch_As_SU_Not_Found_Fail() throws Exception {
        mockMvc.perform(get(String.format("%s?pageNumber=%s&pageSize=%s&names=%s", USERS_BASE_URL, PAGE_NUMBER, PAGE_SIZE, "carter"))
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_ADMIN)))
                .andExpect(status().isNotFound());

        mockMvc.perform(get(String.format("%s?pageNumber=%s&pageSize=%s&userType=%s", USERS_BASE_URL, PAGE_NUMBER, PAGE_SIZE, Role.ADMIN))
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_ADMIN)))
                .andExpect(status().isNotFound());
    }

    @Order(40)
    @Test
    void test_User_Fetch_As_Admin_Success() throws Exception {
        mockMvc.perform(get(String.format("%s?pageNumber=%s&pageSize=%s", USERS_BASE_URL, PAGE_NUMBER, PAGE_SIZE))
                        .header("Authorization", accessTokenAdmin_Full)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_ADMIN)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<UserListResponse> userListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    log.info("{}", userListResponse);
                    assertNotNull(userListResponse.data());
                });
    }

    @Order(41)
    @Test
    void test_User_Fetch_As_Owner_Success() throws Exception {
        mockMvc.perform(get(String.format("%s?pageNumber=%s&pageSize=%s", USERS_BASE_URL, PAGE_NUMBER, PAGE_SIZE))
                        .header("Authorization", accessTokenOwner_Full)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<UserListResponse> userListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    log.info("{}", userListResponse);
                    assertNotNull(userListResponse.data());
                });

        mockMvc.perform(get(String.format("%s?pageNumber=%s&pageSize=%s&names=%s", USERS_BASE_URL, PAGE_NUMBER, PAGE_SIZE, names.getValue()))
                        .header("Authorization", accessTokenOwner_Full)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_ADMIN)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<UserListResponse> userListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    log.info("{}", userListResponse);
                    assertNotNull(userListResponse.data());
                });
    }

    @Order(42)
    @Test
    void test_User_Fetch_As_Owner_Not_Found_Fail() throws Exception {
        mockMvc.perform(get(String.format("%s?pageNumber=%s&pageSize=%s", USERS_BASE_URL, 2, PAGE_SIZE))
                        .header("Authorization", accessTokenOwner_Full)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_ADMIN)))
                .andExpect(status().isNotFound());

        mockMvc.perform(get(String.format("%s?pageNumber=%s&pageSize=%s&names=%s", USERS_BASE_URL, PAGE_NUMBER, PAGE_SIZE, "peterx"))
                        .header("Authorization", accessTokenOwner_Full)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_RIGHT_GROUP_COMMAND_ADMIN)))
                .andExpect(status().isNotFound());
    }

    @Order(43)
    @Test
    void test_User_UserController_CreateUser_As_SU_InvalidPayload_Fail() throws Exception {
        mockMvc.perform(post(USERS_BASE_URL)
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new GenericUserCommand(
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        0,
                                        null,
                                        null
                                ))))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    log.info("{}", apiResponse);
                    assertNotNull(apiResponse.message());
                });

        mockMvc.perform(post(USERS_BASE_URL)
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new GenericUserCommand(
                                        Role.NORMAL,
                                        "pete",
                                        "peter",
                                        "johns",
                                        "sometrandomeail",
                                        254_719_000_000L,
                                        Status.ACTIVE,
                                        List.of(RIGHT_GROUP_ID_1, RIGHT_GROUP_ID_2)
                                ))))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    log.info("{}", apiResponse);
                    assertNotNull(apiResponse.message());
                });

        mockMvc.perform(post(USERS_BASE_URL)
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new GenericUserCommand(
                                        Role.NORMAL,
                                        "pete",
                                        "peter",
                                        "johns",
                                        "sometrandomeail@mail.com",
                                        254_719_000L,
                                        Status.ACTIVE,
                                        List.of(RIGHT_GROUP_ID_1, RIGHT_GROUP_ID_2)
                                ))))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<?> apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                    log.info("{}", apiResponse);
                    assertNotNull(apiResponse.message());
                });
    }

    @Order(44)
    @Test
    void test_User_UserController_CreateUser_Invalid_Rights_Fail() throws Exception {
        mockMvc.perform(post(USERS_BASE_URL)
                        .header("Authorization", accessTokenOwner)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new GenericUserCommand(
                                        Role.NORMAL,
                                        "pete",
                                        "peter",
                                        "johns",
                                        "sometrandomeail@mail.com",
                                        254_719_000_000L,
                                        Status.ACTIVE,
                                        List.of(RIGHT_GROUP_ID_1, RIGHT_GROUP_ID_2)
                                ))))
                .andExpect(status().isForbidden());

        mockMvc.perform(post(USERS_BASE_URL)
                        .header("Authorization", accessTokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new GenericUserCommand(
                                        Role.NORMAL,
                                        "pete",
                                        "peter",
                                        "johns",
                                        "sometrandomeail@mail.com",
                                        254_719_000_000L,
                                        Status.ACTIVE,
                                        List.of(RIGHT_GROUP_ID_1, RIGHT_GROUP_ID_2)
                                ))))
                .andExpect(status().isForbidden());

        mockMvc.perform(post(USERS_BASE_URL)
                        .header("Authorization", accessTokenNormal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new GenericUserCommand(
                                        Role.NORMAL,
                                        "pete",
                                        "peter",
                                        "johns",
                                        "sometrandomeail@mail.com",
                                        254_719_000_000L,
                                        Status.ACTIVE,
                                        List.of(RIGHT_GROUP_ID_1, RIGHT_GROUP_ID_2)
                                ))))
                .andExpect(status().isForbidden());
    }

    @Order(45)
    @Test
    void test_User_UserController_CreateUser_As_SU_While_Creating_Normal_Users_Fail() throws Exception {
        mockMvc.perform(post(USERS_BASE_URL)
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_USER_COMMAND_NORMAL)))
                .andExpect(status().isForbidden());
    }

    @Order(46)
    @Test
    void test_User_UserController_CreateUser_As_Su_Success() throws Exception {
        mockMvc.perform(post(USERS_BASE_URL)
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_USER_COMMAND_SU)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<UserResponse> userResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    log.info("{}", userResponse);
                    assertNotNull(userResponse.data());
                });

        mockMvc.perform(post(USERS_BASE_URL)
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_USER_COMMAND_ADMIN)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<UserResponse> userResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    log.info("{}", userResponse);
                    assertNotNull(userResponse.data());
                });

        mockMvc.perform(post(USERS_BASE_URL)
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_USER_COMMAND_OWNER)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<UserResponse> userResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    log.info("{}", userResponse);
                    assertNotNull(userResponse.data());
                });
    }

    @Order(47)
    @Test
    void test_User_UserController_CreateUser_As_Admin_While_Creating_SU_Admin_Normal_Users_Fail() throws Exception {
        mockMvc.perform(post(USERS_BASE_URL)
                        .header("Authorization", accessTokenAdmin_Full)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_USER_COMMAND_SU)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(USERS_BASE_URL)
                        .header("Authorization", accessTokenAdmin_Full)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_USER_COMMAND_ADMIN)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(USERS_BASE_URL)
                        .header("Authorization", accessTokenAdmin_Full)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_USER_COMMAND_NORMAL)))
                .andExpect(status().isBadRequest());
    }

    @Order(48)
    @Test
    void test_User_UserController_CreateUser_As_Admin_Success() throws Exception {
        mockMvc.perform(post(USERS_BASE_URL)
                        .header("Authorization", accessTokenAdmin_Full)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_USER_COMMAND_OWNER)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<UserResponse> userResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    log.info("{}", userResponse);
                    assertNotNull(userResponse.data());
                });
    }

    @Order(49)
    @Test
    void test_User_UserController_CreateUser_As_Owner_While_Creating_SU_Admin_Owner_Users_Fail() throws Exception {
        mockMvc.perform(post(USERS_BASE_URL)
                        .header("Authorization", accessTokenOwner_Full)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_USER_COMMAND_SU)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(USERS_BASE_URL)
                        .header("Authorization", accessTokenOwner_Full)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_USER_COMMAND_ADMIN)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(USERS_BASE_URL)
                        .header("Authorization", accessTokenOwner_Full)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_USER_COMMAND_OWNER)))
                .andExpect(status().isBadRequest());
    }

    @Order(50)
    @Test
    void test_User_UserController_CreateUser_As_Owner_Success() throws Exception {
        mockMvc.perform(post(USERS_BASE_URL)
                        .header("Authorization", accessTokenOwner_Full)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_USER_COMMAND_NORMAL)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<UserResponse> userResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    log.info("{}", userResponse);
                    assertNotNull(userResponse.data());
                });
    }


    @Order(51)
    @Test
    void test_User_UserController_CreateUser_As_Normal_While_Creating_SU_Admin_Owner_Normal_Users_Fail() throws Exception {
        mockMvc.perform(post(USERS_BASE_URL)
                        .header("Authorization", accessTokenNormal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_USER_COMMAND_SU)))
                .andExpect(status().isForbidden());

        mockMvc.perform(post(USERS_BASE_URL)
                        .header("Authorization", accessTokenNormal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_USER_COMMAND_ADMIN)))
                .andExpect(status().isForbidden());

        mockMvc.perform(post(USERS_BASE_URL)
                        .header("Authorization", accessTokenNormal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_USER_COMMAND_OWNER)))
                .andExpect(status().isForbidden());
        mockMvc.perform(post(USERS_BASE_URL)
                        .header("Authorization", accessTokenNormal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_USER_COMMAND_NORMAL)))
                .andExpect(status().isForbidden());
    }


    @Order(52)
    @Test
    void test_User_UserController_UpdateUser_Invalid_Rights_Fail() throws Exception {
        mockMvc.perform(patch(USERS_BASE_URL + "/" + userSu.getId().getId())
                        .header("Authorization", accessTokenOwner)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new GenericUserCommand(
                                        Role.NORMAL,
                                        "pete",
                                        "peter",
                                        "johns",
                                        "sometrandomeail@mail.com",
                                        254_719_000_000L,
                                        Status.ACTIVE,
                                        List.of(RIGHT_GROUP_ID_1, RIGHT_GROUP_ID_2)
                                ))))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch(USERS_BASE_URL + "/" + userSu.getId().getId())
                        .header("Authorization", accessTokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new GenericUserCommand(
                                        Role.NORMAL,
                                        "pete",
                                        "peter",
                                        "johns",
                                        "sometrandomeail@mail.com",
                                        254_719_000_000L,
                                        Status.ACTIVE,
                                        List.of(RIGHT_GROUP_ID_1, RIGHT_GROUP_ID_2)
                                ))))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch(USERS_BASE_URL + "/" + userSu.getId().getId())
                        .header("Authorization", accessTokenNormal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new GenericUserCommand(
                                        Role.NORMAL,
                                        "pete",
                                        "peter",
                                        "johns",
                                        "sometrandomeail@mail.com",
                                        254_719_000_000L,
                                        Status.ACTIVE,
                                        List.of(RIGHT_GROUP_ID_1, RIGHT_GROUP_ID_2)
                                ))))
                .andExpect(status().isForbidden());
    }

    @Order(53)
    @Test
    void test_User_UserController_UpdateUser_As_Su_Success() throws Exception {
        mockMvc.perform(patch(USERS_BASE_URL + "/" + userSu.getId().getId())
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_USER_COMMAND_SU)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<UserResponse> userResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    log.info("{}", userResponse);
                    assertNotNull(userResponse.data());
                });

        mockMvc.perform(patch(USERS_BASE_URL + "/" + userAdmin.getId().getId())
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_USER_COMMAND_ADMIN)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<UserResponse> userResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    log.info("{}", userResponse);
                    assertNotNull(userResponse.data());
                });

        mockMvc.perform(patch(USERS_BASE_URL + "/" + userOwner.getId().getId())
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_USER_COMMAND_OWNER)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<UserResponse> userResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    log.info("{}", userResponse);
                    assertNotNull(userResponse.data());
                });

        mockMvc.perform(patch(USERS_BASE_URL + "/" + userNormal.getId().getId())
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_USER_COMMAND_NORMAL)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<UserResponse> userResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    log.info("{}", userResponse);
                    assertNotNull(userResponse.data());
                });
    }

    @Order(54)
    @Test
    void test_User_UserController_UpdateUser_As_Admin_While_Updating_SU_Normal_Users_Fail() throws Exception {
        mockMvc.perform(patch(USERS_BASE_URL + "/" + userSu.getId().getId())
                        .header("Authorization", accessTokenAdmin_Full)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_USER_COMMAND_SU)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(patch(USERS_BASE_URL + "/" + userNormal.getId().getId())
                        .header("Authorization", accessTokenAdmin_Full)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_USER_COMMAND_NORMAL)))
                .andExpect(status().isBadRequest());
    }

    @Order(55)
    @Test
    void test_User_UserController_UpdateUser_As_Admin_While_Updating_Another_Admin_Fail() throws Exception {
        mockMvc.perform(patch(USERS_BASE_URL + "/" + userAdmin_EXT.getId().getId())
                        .header("Authorization", accessTokenAdmin_Full)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_USER_COMMAND_ADMIN)))
                .andExpect(status().isBadRequest());
    }

    @Order(56)
    @Test
    void test_User_UserController_UpdateUser_As_Admin_While_Updating_Myself_Success() throws Exception {
        mockMvc.perform(patch(USERS_BASE_URL + "/" + userAdmin_Full.getId().getId())
                        .header("Authorization", accessTokenAdmin_Full)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_USER_COMMAND_ADMIN)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<UserResponse> userResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    log.info("{}", userResponse);
                    assertNotNull(userResponse.data());
                });
    }

    @Order(57)
    @Test
    void test_User_UserController_UpdateUser_As_Owner_While_Updating_SU_Admin_Users_Fail() throws Exception {
        mockMvc.perform(patch(USERS_BASE_URL + "/" + userSu.getId().getId())
                        .header("Authorization", accessTokenOwner_Full)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_USER_COMMAND_SU)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(patch(USERS_BASE_URL + "/" + userAdmin.getId().getId())
                        .header("Authorization", accessTokenOwner_Full)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_USER_COMMAND_ADMIN)))
                .andExpect(status().isBadRequest());
    }

    @Order(58)
    @Test
    void test_User_UserController_UpdateUser_As_Owner_While_Updating_Another_Owner_Fail() throws Exception {
        mockMvc.perform(patch(USERS_BASE_URL + "/" + userOwner.getId().getId())
                        .header("Authorization", accessTokenOwner_Full)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_USER_COMMAND_SU)))
                .andExpect(status().isBadRequest());
    }

    @Order(59)
    @Test
    void test_User_UserController_UpdateUser_As_Owner_While_Updating_Myself_Success() throws Exception {
        mockMvc.perform(patch(USERS_BASE_URL + "/" + userOwner_Full.getId().getId())
                        .header("Authorization", accessTokenOwner_Full)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_USER_COMMAND_OWNER)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<UserResponse> userResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    log.info("{}", userResponse);
                    assertNotNull(userResponse.data());
                });
    }

    @Order(60)
    @Test
    void test_User_UserController_UpdateUser_As_Owner_While_Updating_Another_Owners_Normal_User_Fail() throws Exception {
        mockMvc.perform(patch(USERS_BASE_URL + "/" + userNormal.getId().getId())
                        .header("Authorization", accessTokenOwner_Full)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_USER_COMMAND_SU)))
                .andExpect(status().isBadRequest());
    }

    @Order(61)
    @Test
    void test_User_UserController_UpdateUser_As_Owner_While_Updating_My_Normal_User_Success() throws Exception {
        mockMvc.perform(patch(USERS_BASE_URL + "/" + userNormal_Full.getId().getId())
                        .header("Authorization", accessTokenOwner_Full)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_USER_COMMAND_OWNER)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ApiResponse<UserResponse> userResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    log.info("{}", userResponse);
                    assertNotNull(userResponse.data());
                });
    }

    @Order(62)
    @Test
    void test_User_UserController_UpdateUser_As_Normal_Fail() throws Exception {
        mockMvc.perform(patch(USERS_BASE_URL + "/" + userSu.getId().getId())
                        .header("Authorization", accessTokenNormal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_USER_COMMAND_SU)))
                .andExpect(status().isForbidden());
    }

    @Order(63)
    @Test
    void test_User_UserController_UpdatePassword_Invalid_Payload_Fail() throws Exception {
        mockMvc.perform(patch(USERS_BASE_URL + "/password")
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new PasswordModifyUserCommand("J88U3#"))))
                .andExpect(status().isBadRequest());
    }

    @Order(64)
    @Test
    void test_User_UserController_UpdatePassword_Success() throws Exception {
        mockMvc.perform(patch(USERS_BASE_URL + "/password")
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(PASSWORD_MODIFY_USER_COMMAND)))
                .andExpect(status().isOk());

        mockMvc.perform(patch(USERS_BASE_URL + "/password")
                        .header("Authorization", accessTokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(PASSWORD_MODIFY_USER_COMMAND)))
                .andExpect(status().isOk());

        mockMvc.perform(patch(USERS_BASE_URL + "/password")
                        .header("Authorization", accessTokenOwner)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(PASSWORD_MODIFY_USER_COMMAND)))
                .andExpect(status().isOk());

        mockMvc.perform(patch(USERS_BASE_URL + "/password")
                        .header("Authorization", accessTokenNormal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(PASSWORD_MODIFY_USER_COMMAND)))
                .andExpect(status().isOk());
    }

    @Order(65)
    @Test
    void test_User_UserController_DeleteUser_Yourself_Fail() throws Exception {
        mockMvc.perform(delete(USERS_BASE_URL + "/" + userSu.getId().getId())
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(delete(USERS_BASE_URL + "/" + userAdmin.getId().getId())
                        .header("Authorization", accessTokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(delete(USERS_BASE_URL + "/" + userOwner.getId().getId())
                        .header("Authorization", accessTokenOwner)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Order(66)
    @Test
    void test_User_UserController_DeleteUser_As_Su_Success() throws Exception {

        mockMvc.perform(delete(USERS_BASE_URL + "/" + userAdmin.getId().getId())
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(delete(USERS_BASE_URL + "/" + userOwner.getId().getId())
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(delete(USERS_BASE_URL + "/" + userNormal.getId().getId())
                        .header("Authorization", accessTokenSu)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(GENERIC_USER_COMMAND_NORMAL)))
                .andExpect(status().isOk());
    }

    @Order(67)
    @Test
    void test_User_UserController_DeleteUser_As_Admin_DeleteOther_Su_Admin_Normal_Fail() throws Exception {
        mockMvc.perform(delete(USERS_BASE_URL + "/" + userSu.getId().getId())
                        .header("Authorization", accessTokenAdmin_Full)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(delete(USERS_BASE_URL + "/" + userAdmin.getId().getId())
                        .header("Authorization", accessTokenAdmin_Full)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(delete(USERS_BASE_URL + "/" + userNormal.getId().getId())
                        .header("Authorization", accessTokenAdmin_Full)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Order(68)
    @Test
    void test_User_UserController_DeleteUser_As_Admin_Success() throws Exception {

        mockMvc.perform(delete(USERS_BASE_URL + "/" + userOwner.getId().getId())
                        .header("Authorization", accessTokenAdmin_Full)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Order(69)
    @Test
    void test_User_UserController_DeleteUser_As_Owner_DeleteOther_Su_Admin_Owner_Fail() throws Exception {
        mockMvc.perform(delete(USERS_BASE_URL + "/" + userSu.getId().getId())
                        .header("Authorization", accessTokenOwner_Full)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(delete(USERS_BASE_URL + "/" + userAdmin.getId().getId())
                        .header("Authorization", accessTokenOwner_Full)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(delete(USERS_BASE_URL + "/" + userOwner_2.getId().getId())
                        .header("Authorization", accessTokenOwner_Full)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Order(70)
    @Test
    void test_User_UserController_DeleteUser_As_Owner_Delete_Other_Owners_Normal_User_Fail() throws Exception {

        mockMvc.perform(delete(USERS_BASE_URL + "/" + userNormal.getId().getId())
                        .header("Authorization", accessTokenOwner_Full)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Order(71)
    @Test
    void test_User_UserController_DeleteUser_As_Owner_Delete_My_Own_Normal_User_Success() throws Exception {

        mockMvc.perform(delete(USERS_BASE_URL + "/" + userNormal_Full.getId().getId())
                        .header("Authorization", accessTokenOwner_Full)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

}



