package com.smis.user.application.rest;

import com.smis.common.core.dto.ApiResponse;
import com.smis.common.core.util.Role;
import com.smis.user.domain.dto.user.GenericUserCommand;
import com.smis.user.domain.dto.user.LoginPayload;
import com.smis.user.domain.dto.user.LoginResponse;
import com.smis.user.domain.dto.user.UserResponse;
import com.smis.user.domain.ports.input.service.UserApplicationService;
import com.smis.user.domain.util.Status;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.smis.common.core.util.Helpers.INTERNAL_EXECUTION_USER;
import static com.smis.common.core.util.Helpers.ROLE_OWNER_DEFAULT_RIGHT_GROUP_ID;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final UserApplicationService userApplicationService;

    @PostMapping("/login")
    ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginPayload loginPayload) {
        return ResponseEntity.ok(new ApiResponse<>(null, userApplicationService.login(loginPayload)));
    }

    @PostMapping("/register")
    ResponseEntity<ApiResponse<Void>> publicOwnerRegistration(@RequestBody GenericUserCommand genericUserCommand) {
        UserResponse userResponse = userApplicationService.createUser(INTERNAL_EXECUTION_USER,
                new GenericUserCommand(
                        Role.OWNER,
                        genericUserCommand.username(),
                        genericUserCommand.firstName(),
                        genericUserCommand.otherNames(),
                        genericUserCommand.email(),
                        genericUserCommand.phoneNumber(),
                        Status.ACTIVE,
                        List.of(ROLE_OWNER_DEFAULT_RIGHT_GROUP_ID)
                ));
        return ResponseEntity.ok(new ApiResponse<>("Account successfully created, check your email for initial password.", null));
    }
}
