package com.smis.user.domain.dto.rightgroup;

import com.smis.common.core.util.Right;

import java.util.List;
import java.util.UUID;

public record RightGroupResponse(UUID id, String name,
                                 List<Right> rights) {
}
