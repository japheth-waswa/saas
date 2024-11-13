package com.smis.user.domain.config;

import com.smis.common.core.registry.OwnerManagedRightsRegistry;
import com.smis.common.core.util.Right;
import com.smis.common.core.util.Role;
import com.smis.common.core.valueobject.UserId;
import com.smis.common.core.valueobject.UserRight;
import com.smis.common.core.valueobject.UserType;
import com.smis.security.util.SecurityHelper;
import com.smis.user.domain.entity.RightGroup;
import com.smis.user.domain.entity.User;
import com.smis.user.domain.ports.output.repository.RightGroupRepository;
import com.smis.user.domain.ports.output.repository.UserRepository;
import com.smis.user.domain.util.Status;
import com.smis.user.domain.valueobject.*;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

import static com.smis.common.core.util.Helpers.*;

@Slf4j
@Configuration
@AllArgsConstructor
public class UserApplicationServiceConfig {
    private final OwnerManagedRightsRegistry ownerManagedRightsRegistry;
    private final RightGroupRepository rightGroupRepository;
    private final UserRepository userRepository;
    private final SecurityHelper securityHelper;

    @PostConstruct
    void registerOwnerManagedRights() {
        ownerManagedRightsRegistry.registerOwnerManagedRights(Set.of(new UserRight(Right.ACCOUNTING_PAYMENT_VOUCHER_CREATE)));
    }

    @PostConstruct
    void createDefaultRepositoryEntities(){
        try{
            //create default owner right group
            rightGroupRepository.create(RightGroup.builder()
                            .rightGroupId(new RightGroupId(ROLE_OWNER_DEFAULT_RIGHT_GROUP_ID))
                            .creatorUserType(new UserType(Role.SU))
                            .creatorUserId(new UserId(SUPER_USER_ID))
                            .name("Owner Default Right Group")
                    .build());
        }catch(Exception e){
            log.error("Error while creating default right group",e);
        }

        try{
            userRepository.save(User.builder()
                            .userId(new UserId(SUPER_USER_ID))
                            .userType(new UserType(Role.SU))
                            .username(new Username(SUPER_USER_USERNAME))
                            .firstname(new Firstname(SUPER_USER_USERNAME))
                            .otherNames(new OtherNames(SUPER_USER_USERNAME))
                            .password(new Password(securityHelper.encodePassword(SUPER_USER_PASSWORD)))
                            .email(new Email(SUPER_USER_EMAIL))
                            .phoneNumber(new PhoneNumber(SUPER_USER_PHONE))
                            .userStatus(new UserStatus(Status.ACTIVE))
                    .build());
        }catch(Exception e){
            log.error("Error while creating default user",e);
        }

    }
}
