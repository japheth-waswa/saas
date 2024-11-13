package com.smis.user.domain.dto.rightgroup;

import java.util.List;

public record RightGroupListResponse(long totalCount, List<RightGroupResponse> rightGroups) {
}
