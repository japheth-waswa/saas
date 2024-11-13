package com.smis.container;

import com.smis.common.core.util.YamlPropertySourceFactory;
import com.smis.user.domain.ports.output.repository.RightGroupRepository;
import com.smis.user.domain.ports.output.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.mockito.Mockito;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan(basePackages = "com.smis")
@PropertySources({
        @PropertySource(value = "classpath:security-application.yml", factory = YamlPropertySourceFactory.class),
})
@RequiredArgsConstructor
public class ControllersTestConfiguration {

    @Bean
    RightGroupRepository rightGroupRepository() {
        return Mockito.mock(RightGroupRepository.class);
    }

    @Bean
    UserRepository userRepository() {
        return Mockito.mock(UserRepository.class);
    }
}
