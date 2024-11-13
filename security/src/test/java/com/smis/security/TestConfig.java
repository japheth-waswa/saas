package com.smis.security;

import com.smis.common.core.registry.PublicUrlRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class TestConfig {
    private final PublicUrlRegistry publicUrlRegistry;

    @PostConstruct
    void registerPublicURLs() {
        publicUrlRegistry.registerPublicUrls(Set.of("/test/login", "/test/public"));
    }
}
