package com.smis.user.domain.dto.user;

import java.util.List;

public record UserListResponse(long totalCount, List<UserResponse> users) {
}
