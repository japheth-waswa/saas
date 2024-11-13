package com.smis.container.util;

import com.smis.user.domain.UserDomainService;
import com.smis.user.domain.UserDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public UserDomainService userDomainService(){
        return new UserDomainServiceImpl();
    }
}
