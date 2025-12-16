package com.calendar.gateway.configuration;

import com.calendar.gateway.domain.ports.CalendarUsersApiPort;
import com.calendar.gateway.domain.ports.RedisIdentityCachePort;
import com.calendar.gateway.domain.services.IdentityTranslatorService;
import com.calendar.gateway.domain.services.implementation.IdentityTranslatorServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayApplicationConfig {

    @Bean
    public IdentityTranslatorService identityTranslatorService(CalendarUsersApiPort calendarUsersApiPort, RedisIdentityCachePort redisIdentityCachePort) {
        return new IdentityTranslatorServiceImpl(calendarUsersApiPort, redisIdentityCachePort);
    }
}
