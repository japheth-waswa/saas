package com.smis.common.core.util;

import com.smis.common.core.dto.LoggedInUser;
import com.smis.common.core.valueobject.ExecutionUser;
import com.smis.common.core.valueobject.UserId;
import com.smis.common.core.valueobject.UserType;
import jakarta.validation.ConstraintViolationException;

import java.util.UUID;
import java.util.stream.Collectors;

public final class Helpers {
    private Helpers() {
    }

    public static String DEFAULT_ERROR_MESSAGE = "Unexpected error occurred!";
    public static String ERROR_DUPLICATE_RECORD_MESSAGE = "Duplicate record";
    public static long SESSION_EXPIRY = 3600L;
    public static final UUID SUPER_USER_ID = UUID.fromString("cd48daf8-02ee-421a-984f-cdea896ad951");
    public static final String SUPER_USER_USERNAME = "superuser";
    public static final String SUPER_USER_PASSWORD = "h*3y@D0k3S^lq2";
    public static final String SUPER_USER_EMAIL = "superuser@mail.com";
    public static final long SUPER_USER_PHONE = 254_000_000_000L;
    public static final ExecutionUser INTERNAL_EXECUTION_USER = new ExecutionUser(new UserId(SUPER_USER_ID), new UserType(Role.SU));
    public static final UUID ROLE_OWNER_DEFAULT_RIGHT_GROUP_ID = UUID.fromString("ea3ee0e7-9e0b-4a01-b9e5-bc1d606d3cb2");
    public static final String INVALID_LOGIN_CREDENTIALS_MESSAGE = "Invalid username or password";

    public static String extractViolationsFromException(ConstraintViolationException validationException) {
        return validationException.getConstraintViolations()
                .stream()
                .map(constraintViolation -> {
                    String[] cvPath = constraintViolation.getPropertyPath().toString().split("\\.");
                    return String.format("%s %s", cvPath[cvPath.length - 1], constraintViolation.getMessage());
//                    return String.format("%s", constraintViolation.getMessage());
                })
                .collect(Collectors.joining(" | "));
    }

    public static ExecutionUser buildExecutionUser(LoggedInUser loggedInUser) {
        return new ExecutionUser(new UserId(loggedInUser.userId()), new UserType(loggedInUser.userType()));
    }

    public static boolean isOwner(ExecutionUser executionUser) {
        return executionUser.getUserType().getValue().equals(Role.OWNER);
    }

}
