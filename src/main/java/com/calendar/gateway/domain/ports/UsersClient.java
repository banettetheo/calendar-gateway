package com.calendar.gateway.domain.ports;

import reactor.core.publisher.Mono;

public interface UsersClient {

    Mono<Long> fetchInternalUserId(String keycloakId);
}
