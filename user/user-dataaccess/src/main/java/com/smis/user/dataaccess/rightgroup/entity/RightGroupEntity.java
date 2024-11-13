package com.smis.user.dataaccess.rightgroup.entity;

import com.smis.common.core.util.Role;
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
@Table(name = "right_groups", indexes = {
        @Index(name = "idx_name", columnList = "name"),
        @Index(name = "idx_rights", columnList = "rights"),
        @Index(name = "idx_creatorUserId", columnList = "creatorUserId"),
        @Index(name = "idx_creatorUserType", columnList = "creatorUserType"),
        @Index(name = "idx_createdAt", columnList = "createdAt"),
        @Index(name = "idx_updatedAt", columnList = "updatedAt"),
        @Index(name = "idx_deletedAt", columnList = "deletedAt"),
})
public class RightGroupEntity {

    @Id
    private UUID id;

    private String name;

    @Column(length = 2000)
    private String rights;

    private UUID creatorUserId;

    @Enumerated(EnumType.STRING)
    private Role creatorUserType;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
}
