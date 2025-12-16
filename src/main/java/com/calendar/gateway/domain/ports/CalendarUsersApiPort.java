package com.calendar.gateway.domain.ports;

import reactor.core.publisher.Mono;

public interface CalendarUsersApiPort {

    Mono<Long> fetchInternalUserId(String keycloakId);
}
