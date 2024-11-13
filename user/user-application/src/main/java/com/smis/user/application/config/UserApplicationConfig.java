package com.smis.user.application.config;

import com.smis.common.core.registry.PublicUrlRegistry;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
@AllArgsConstructor
public class UserApplicationConfig {
    private final PublicUrlRegistry publicUrlRegistry;

    @PostConstruct
    void registerPublicURLs() {
        publicUrlRegistry.registerPublicUrls(Set.of("/auth/login","/auth/register"));
    }

}
