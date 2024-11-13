package com.smis.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.smis.security.util.JwtUtil;
import com.smis.security.util.SecurityHelper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

record LoggedInUser(long id, String username, String gender, long age, long memberSince) {
}

record Dosage(long unit, String title) {
}

@Slf4j
@RestController
@RequestMapping("/test")
class TestController {

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    SecurityHelper securityHelper;

    @GetMapping("/login")
    String loginEndpoint() {
        return jwtUtil.generateJwt(3600L, "jane_doe",
                List.of("user_read", "user_write", "report_download"),
                new LoggedInUser(853, "jane_doe", "male", 76, LocalDateTime.now().minusYears(7).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()),
                Map.of("LOCATION", "Kilimani, Embakasi",
                        "dose", new Dosage(3, "mg")));
    }

    @GetMapping("/public")
    String publicEndpoint() {
        return "Public endpoint";
    }

    @GetMapping("/private")
    String privateEndpoint() {
        return "Private endpoint";
    }

    @GetMapping("/read-user")
    @PreAuthorize("hasAuthority('user_read')")
    String privateReadUser() {
        return "Private read user endpoint";
    }

    @GetMapping("/write-user")
    @PreAuthorize("hasAuthority('user_write')")
    String privateWriteUser() {
        return "Private write user endpoint";
    }

    @GetMapping("/download-report")
    @PreAuthorize("hasAuthority('report_download')")
    String privateDownloadReport() {
        return "Private download report endpoint";
    }

    @GetMapping("/rand")
    @PreAuthorize("hasAuthority('rand_authority')")
    String privateRandomAuthority() {
        return "Private random authority endpoint";
    }

    @GetMapping("/auth-claims")
    @PreAuthorize("hasAuthority('user_write')")
    String privateWriteUserAuth(@AuthenticationPrincipal Jwt jwt) {
//        LoggedInUser loggedInUser = securityHelper.convertLinkedTreeMapToObject(jwt.getClaim("user"),LoggedInUser.class);
        LoggedInUser loggedInUser = securityHelper.getJwtUserClaim(jwt, LoggedInUser.class);
        String location = jwt.getClaim("LOCATION");
        Dosage dose = securityHelper.convertLinkedTreeMapToObject(jwt.getClaim("dose"), Dosage.class);

        log.info("The claims: {}", jwt.getClaims());
        log.info("The loggedInUser: {}", loggedInUser);
        log.info("The loggedInUser gender: {}", loggedInUser.gender());
        log.info("The location: {}", location);
        log.info("The dose: {}", dose);
        log.info("The dose title: {}", dose.title());
        return "Private auth payload endpoint";
    }
}
