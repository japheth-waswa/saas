package com.smis.security.dto;

import com.smis.common.core.dto.LoggedInUser;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

public record AuthenticatedUserPayload(Collection<GrantedAuthority> authorities,
                                       String username,
                                       LoggedInUser loggedInUser,
                                       Map<String, Object> extraClaims) {
}
