package com.smis.user.domain.dto.user;

import com.smis.common.core.util.Role;
import com.smis.user.domain.util.Status;
import com.smis.user.domain.validator.ExactLength;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record GenericUserCommand(@NotNull Role userType,
                                 @NotBlank String username,
                                 @NotBlank String firstName,
                                 @NotBlank String otherNames,
                                 @NotNull @Email String email,
                                 @ExactLength(length = 12, message = "Phone number must be exactly 12 digits long")
                                 long phoneNumber,
                                 @NotNull Status status,
                                 @NotEmpty List<UUID> rightGroupIds) {
}
