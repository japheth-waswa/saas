package com.smis.user.dataaccess;

import com.smis.common.core.util.YamlPropertySourceFactory;
import com.smis.user.domain.UserDomainService;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication(scanBasePackages = "com.smis")
@PropertySources({
        @PropertySource(value = "classpath:user-dataaccess-application.yml", factory = YamlPropertySourceFactory.class),
        @PropertySource(value = "classpath:security-application.yml", factory = YamlPropertySourceFactory.class)
})
public class UserDataAccessTestConfiguration {

    @Bean
    UserDomainService userDomainService() {
        return Mockito.mock(UserDomainService.class);
    }

}
