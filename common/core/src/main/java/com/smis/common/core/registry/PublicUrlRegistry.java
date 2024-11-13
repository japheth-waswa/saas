package com.smis.common.core.registry;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@Getter
public class PublicUrlRegistry {
    private final Set<String> publicURLs = new HashSet<>();

    public void registerPublicUrls(Set<String> publicURLs) {
        this.publicURLs.addAll(publicURLs);
    }
}
