package com.smis.user.application.rest;

import com.smis.common.core.dto.ApiResponse;
import com.smis.common.core.dto.LoggedInUser;
import com.smis.common.core.registry.OwnerManagedRightsRegistry;
import com.smis.common.core.util.Helpers;
import com.smis.common.core.util.Right;
import com.smis.common.core.valueobject.ExecutionUser;
import com.smis.security.util.SecurityHelper;
import com.smis.user.application.dto.RightListItem;
import com.smis.user.domain.dto.rightgroup.GenericRightGroupCommand;
import com.smis.user.domain.dto.rightgroup.RightGroupListResponse;
import com.smis.user.domain.dto.rightgroup.RightGroupResponse;
import com.smis.user.domain.ports.input.service.RightGroupApplicationService;
import com.smis.user.domain.valueobject.RightGroupId;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/right-group")
public class RightGroupController {
    private final OwnerManagedRightsRegistry ownerManagedRightsRegistry;
    private final SecurityHelper securityHelper;
    private final RightGroupApplicationService rightGroupApplicationService;

    @GetMapping("/rights")
    @PreAuthorize("hasAuthority('SU') or hasAuthority('ADMIN') or hasAuthority('OWNER')")
    ResponseEntity<ApiResponse<List<RightListItem>>> fetchRights(@AuthenticationPrincipal Jwt jwt) {
        var authenticatedUserPayload = securityHelper.buildAuthenticatedUserPayload(jwt);

        List<RightListItem> rightListItems;
        if (securityHelper.isOwner(authenticatedUserPayload)) {
            rightListItems = ownerManagedRightsRegistry.getOwnerManagedRights()
                    .stream()
                    .map(userRight -> new RightListItem(userRight.getValue(), userRight.getValue().getDescription()))
                    .toList();
        } else {
            rightListItems = Stream.of(Right.values())
                    .map(right -> new RightListItem(right, right.getDescription()))
                    .toList();
        }

        return ResponseEntity.ok(new ApiResponse<>(null,
                rightListItems));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SU') or hasAuthority('OWNER') or @securityHelper.hasAuthorityAndAnyRight(authentication, 'ADMIN', 'RIGHT_GROUP_READ')")
    ResponseEntity<ApiResponse<RightGroupListResponse>> fetchRightGroups(@AuthenticationPrincipal Jwt jwt, @RequestParam int pageNumber, @RequestParam int pageSize) {
        var authenticatedUserPayload = securityHelper.buildAuthenticatedUserPayload(jwt);
        return ResponseEntity.ok(new ApiResponse<>(null, rightGroupApplicationService.findAllRightGroups(Helpers.buildExecutionUser(authenticatedUserPayload.loggedInUser())
                , pageNumber,
                pageSize)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SU') or hasAuthority('OWNER') or @securityHelper.hasAuthorityAndAnyRight(authentication, 'ADMIN', 'RIGHT_GROUP_CREATE')")
    ResponseEntity<ApiResponse<RightGroupResponse>> createRightGroup(@AuthenticationPrincipal Jwt jwt, @RequestBody GenericRightGroupCommand genericRightGroupCommand) {
        LoggedInUser loggedInUser = securityHelper.getJwtUserClaim(jwt, LoggedInUser.class);
        ExecutionUser executionUser = Helpers.buildExecutionUser(loggedInUser);
        return ResponseEntity.ok(new ApiResponse<>(null, rightGroupApplicationService.createRightGroup(executionUser,
                genericRightGroupCommand)));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('SU') or hasAuthority('ADMIN') or hasAuthority('OWNER')")
    ResponseEntity<ApiResponse<RightGroupResponse>> updateRightGroup(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id, @RequestBody GenericRightGroupCommand genericRightGroupCommand) {
        LoggedInUser loggedInUser = securityHelper.getJwtUserClaim(jwt, LoggedInUser.class);
        ExecutionUser executionUser = Helpers.buildExecutionUser(loggedInUser);
        return ResponseEntity.ok(new ApiResponse<>(null, rightGroupApplicationService.updateRightGroup(executionUser,
                new RightGroupId(id),
                genericRightGroupCommand)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SU') or hasAuthority('ADMIN') or hasAuthority('OWNER')")
    ResponseEntity<Void> deleteRightGroup(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        LoggedInUser loggedInUser = securityHelper.getJwtUserClaim(jwt, LoggedInUser.class);
        ExecutionUser executionUser = Helpers.buildExecutionUser(loggedInUser);
        rightGroupApplicationService.deleteRightGroup(executionUser, new RightGroupId(id));
        return ResponseEntity.ok().build();
    }
}
