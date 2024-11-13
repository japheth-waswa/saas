package com.smis.security.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Getter
@Setter
@NoArgsConstructor
@Configuration
@ConfigurationProperties("jwt")
public class ApplicationPropertiesConfig {
    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;
}
