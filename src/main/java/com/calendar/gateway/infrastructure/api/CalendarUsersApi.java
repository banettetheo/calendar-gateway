package com.calendar.gateway.infrastructure.api;

import com.calendar.gateway.infrastructure.dtos.BusinessUserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

@HttpExchange("users")
public interface CalendarUsersApi {

    @GetExchange("resolve")
    Mono<ResponseEntity<Long>> resolveInternalUserId(@RequestHeader("X-Keycloak-Sub") String keycloakId);

}
