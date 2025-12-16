package com.calendar.gateway.domain.services.implementation;

import com.calendar.gateway.domain.ports.CalendarUsersApiPort;
import com.calendar.gateway.domain.ports.RedisIdentityCachePort;
import com.calendar.gateway.domain.services.IdentityTranslatorService;
import reactor.core.publisher.Mono;

public class IdentityTranslatorServiceImpl implements IdentityTranslatorService {

    private final CalendarUsersApiPort calendarUsersApiPort;
    private final RedisIdentityCachePort redisIdentityCachePort;

    public IdentityTranslatorServiceImpl(CalendarUsersApiPort calendarUsersApiPort, RedisIdentityCachePort redisIdentityCachePort) {
        this.calendarUsersApiPort = calendarUsersApiPort;
        this.redisIdentityCachePort = redisIdentityCachePort;
    }

    @Override
    public Mono<Long> getInternalId(String keycloakId) {
        return redisIdentityCachePort.findByExternalId(keycloakId)
                .switchIfEmpty(
                        calendarUsersApiPort.fetchInternalUserId(keycloakId)
                                .flatMap(internalId ->
                                        redisIdentityCachePort.saveMapping(keycloakId, internalId)
                                                .thenReturn(internalId)
                                )
                );
    }
}
