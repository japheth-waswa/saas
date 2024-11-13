package com.smis.security.util;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Validated
public class JwtUtil {
    private final JwtEncoder encoder;

    public String generateJwt(long expiryInSeconds, @NotBlank String username, @NotEmpty List<String> authorities, Object user, Map<String, Object> extraClaims) {
        Instant now = Instant.now();
        JwtClaimsSet.Builder builder = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiryInSeconds))
                .subject(username)
                .claim("authorities", authorities);

        if (user != null) {
            builder.claim("user", user);
        }

        if (extraClaims != null && !extraClaims.isEmpty()) {
            extraClaims.forEach((key, value) -> {
                if (key.equals("authorities") || key.equals("user")) {
                    throw new IllegalArgumentException("Cannot use 'authorities' or 'user' as a key in extraClaims");
                }
                builder.claim(key, value);
            });
        }

        return encoder.encode(JwtEncoderParameters.from(builder.build())).getTokenValue();
    }
}
