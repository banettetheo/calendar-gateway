package com.calendar.gateway.infrastructure.api;

import com.calendar.gateway.infrastructure.dtos.BusinessUserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import reactor.core.publisher.Mono;

@HttpExchange("users")
public interface CalendarUsersApi {

    @GetExchange("me")
    Mono<ResponseEntity<BusinessUserDTO>> readProfile(@RequestHeader("X-Keycloak-Sub") String keycloakId);
}
