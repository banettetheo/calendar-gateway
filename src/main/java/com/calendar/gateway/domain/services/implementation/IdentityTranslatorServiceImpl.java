package com.calendar.gateway.domain.services.implementation;

import com.calendar.gateway.domain.ports.UsersClient;
import com.calendar.gateway.domain.ports.CacheRepository;
import com.calendar.gateway.domain.services.IdentityTranslatorService;
import reactor.core.publisher.Mono;

public class IdentityTranslatorServiceImpl implements IdentityTranslatorService {

    private final UsersClient usersClient;
    private final CacheRepository cacheRepository;

    public IdentityTranslatorServiceImpl(UsersClient usersClient, CacheRepository cacheRepository) {
        this.usersClient = usersClient;
        this.cacheRepository = cacheRepository;
    }

    @Override
    public Mono<Long> getInternalId(String keycloakId) {
        return cacheRepository.findByExternalId(keycloakId)
                .switchIfEmpty(
                        usersClient.fetchInternalUserId(keycloakId)
                                .flatMap(internalId ->
                                        cacheRepository.saveMapping(keycloakId, internalId)
                                                .thenReturn(internalId)
                                )
                );
    }
}
