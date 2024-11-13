package com.smis.container;

import com.smis.common.core.util.YamlPropertySourceFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {"com.smis"})
@EntityScan(basePackages = {"com.smis"})
@PropertySources({
        @PropertySource(value = "classpath:user-dataaccess-application.yml", factory = YamlPropertySourceFactory.class),
        @PropertySource(value = "classpath:security-application.yml", factory = YamlPropertySourceFactory.class)
})
@SpringBootApplication(scanBasePackages = "com.smis")
public class RootApplication {
    public static void main(String[] args) {
        SpringApplication.run(RootApplication.class, args);
    }
}
