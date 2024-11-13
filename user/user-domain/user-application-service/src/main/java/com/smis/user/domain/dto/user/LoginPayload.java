package com.smis.user.domain.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static com.smis.user.domain.util.UserDomainConstants.PASSWORD_PATTERN;
import static com.smis.user.domain.util.UserDomainConstants.PASSWORD_PATTERN_ERROR_MESSAGE;

public record LoginPayload(@NotBlank String username,
                           @Pattern(regexp = PASSWORD_PATTERN,
                                   message = PASSWORD_PATTERN_ERROR_MESSAGE)
                           @NotBlank String password) {
}
