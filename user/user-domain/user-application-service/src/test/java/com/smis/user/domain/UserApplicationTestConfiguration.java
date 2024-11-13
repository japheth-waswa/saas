package com.smis.user.domain;

import com.smis.common.core.util.YamlPropertySourceFactory;
import com.smis.user.domain.ports.output.repository.RightGroupRepository;
import com.smis.user.domain.ports.output.repository.UserRepository;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication(scanBasePackages = "com.smis")
@PropertySources({
        @PropertySource(value = "classpath:security-application.yml", factory = YamlPropertySourceFactory.class)
})
public class UserApplicationTestConfiguration {

    @Bean
    UserDomainService userDomainService() {
        return new UserDomainServiceImpl();
    }

    @Bean
    RightGroupRepository rightGroupRepository() {
        return Mockito.mock(RightGroupRepository.class);
    }

    @Bean
    UserRepository userRepository() {
        return Mockito.mock(UserRepository.class);
    }
}
