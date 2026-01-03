package com.calendar.gateway.domain.ports;

import reactor.core.publisher.Mono;

public interface CacheRepository {

    Mono<Long> findByExternalId(String keycloakId);

    Mono<Void> saveMapping(String keycloakId, Long internalId);
}
