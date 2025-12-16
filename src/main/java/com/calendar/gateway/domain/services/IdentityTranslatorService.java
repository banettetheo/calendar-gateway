package com.calendar.gateway.domain.services;

import reactor.core.publisher.Mono;

public interface IdentityTranslatorService {

    Mono<Long> getInternalId(String keycloakId);
}
