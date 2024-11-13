package com.smis.user.dataaccess.user.entity;


import com.smis.common.core.util.Role;
import com.smis.user.domain.util.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_userType", columnList = "userType"),
        @Index(name = "idx_firstname", columnList = "firstname"),
        @Index(name = "idx_otherNames", columnList = "otherNames"),
        @Index(name = "idx_password", columnList = "password"),
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_phoneNumber", columnList = "phoneNumber"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_rightGroupIds", columnList = "rightGroupIds"),
        @Index(name = "idx_ownerId", columnList = "ownerId"),
        @Index(name = "idx_createdAt", columnList = "createdAt"),
        @Index(name = "idx_updatedAt", columnList = "updatedAt"),
        @Index(name = "idx_deletedAt", columnList = "deletedAt"),
})
public class UserEntity {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Role userType;

    @Column(unique = true)
    private String username;

    private String firstname;
    private String otherNames;
    private String password;
    private String email;
    private long phoneNumber;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(length = 2000)
    private String rightGroupIds;

    private UUID ownerId;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
}
