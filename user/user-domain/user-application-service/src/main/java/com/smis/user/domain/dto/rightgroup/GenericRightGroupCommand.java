package com.smis.user.domain.dto.rightgroup;

import com.smis.common.core.util.Right;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record GenericRightGroupCommand(@NotBlank String name, @NotEmpty List<Right> rights) {
}
