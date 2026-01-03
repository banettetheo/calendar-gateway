package com.calendar.gateway.configuration;

import com.calendar.gateway.domain.ports.UsersClient;
import com.calendar.gateway.domain.ports.CacheRepository;
import com.calendar.gateway.domain.services.IdentityTranslatorService;
import com.calendar.gateway.domain.services.implementation.IdentityTranslatorServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayApplicationConfig {

    @Bean
    public IdentityTranslatorService identityTranslatorService(UsersClient usersClient, CacheRepository cacheRepository) {
        return new IdentityTranslatorServiceImpl(usersClient, cacheRepository);
    }
}
