package com.smis.security.util;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.internal.LinkedTreeMap;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import com.smis.common.core.dto.LoggedInUser;
import com.smis.common.core.util.Role;
import com.smis.security.dto.AuthenticatedUserPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SecurityHelper {
    private final PasswordEncoder passwordEncoder;
    private final Gson gson = new Gson();


    public <T> T convertLinkedTreeMapToObject(LinkedTreeMap<String, Object> map, Class<T> clazz) {
        return gson.fromJson(gson.toJson(map), clazz);
    }

    public <T> T convertLinkedTreeMapToCollection(Object map, Type type) {
        return gson.fromJson(gson.toJson(map), type);
    }

    public <T> T getJwtUserClaim(Jwt jwt, Class<T> clazz) {
        return convertLinkedTreeMapToObject(jwt.getClaim("user"), clazz);
    }

    public AuthenticatedUserPayload buildAuthenticatedUserPayload(Jwt jwt) {
        Type listType = new TypeToken<List<String>>() {
        }.getType();
        List<String> authoritiesStrings = convertLinkedTreeMapToCollection(jwt.getClaim("authorities"), listType);
        List<GrantedAuthority> authorities = authoritiesStrings.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new AuthenticatedUserPayload(authorities,
                jwt.getSubject(),
                getJwtUserClaim(jwt, LoggedInUser.class),
                jwt.getClaims());
    }

    public boolean isOwner(AuthenticatedUserPayload authenticatedUserPayload) {
        return authenticatedUserPayload.authorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(Role.OWNER.toString()::equals);
    }

    public boolean passwordMatches(CharSequence rawPassword,
                                   String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean hasAuthorityAndAnyRight(Authentication authentication,String authority,String... rights){
        Set<String> rightsSet = new HashSet<>(List.of(rights));
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority)) &&
                authentication.getAuthorities().stream()
                        .anyMatch(grantedAuthority -> rightsSet.contains(grantedAuthority.getAuthority()));

    }

}
