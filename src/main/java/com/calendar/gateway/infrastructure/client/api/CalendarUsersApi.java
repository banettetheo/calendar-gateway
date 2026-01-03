package com.calendar.gateway.infrastructure.client.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import reactor.core.publisher.Mono;

@HttpExchange("profile")
public interface CalendarUsersApi {

    @GetExchange("resolve")
    Mono<ResponseEntity<Long>> resolveInternalUserId(@RequestHeader("X-Keycloak-Sub") String keycloakId);

}
