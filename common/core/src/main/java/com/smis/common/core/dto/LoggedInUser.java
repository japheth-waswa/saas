package com.smis.common.core.dto;


import com.smis.common.core.util.Role;

import java.util.UUID;

public record LoggedInUser(UUID userId, Role userType, String fullNames) {
}
