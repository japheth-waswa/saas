package com.smis.user.domain.dto.user;

import com.smis.common.core.util.Role;
import com.smis.user.domain.dto.rightgroup.RightGroupResponse;
import com.smis.user.domain.util.Status;

import java.util.List;
import java.util.UUID;

public record UserResponse(UUID userId,
                           Role userType,
                           String username,
                           String password,
                           String firstName,
                           String otherNames,
                           String email,
                           long phoneNumber,
                           Status status,
                           UUID ownerId,
                           List<RightGroupResponse> rightGroups) {
}
