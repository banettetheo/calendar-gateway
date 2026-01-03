package com.calendar.gateway.infrastructure.client.adapters;

import com.calendar.gateway.domain.ports.UsersClient;
import com.calendar.gateway.infrastructure.client.api.CalendarUsersApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class UsersApiAdapter implements UsersClient {

    private final CalendarUsersApi calendarUsersApi;

    public UsersApiAdapter(CalendarUsersApi calendarUsersApi) {
        this.calendarUsersApi = calendarUsersApi;
    }

    @Override
    public Mono<Long> fetchInternalUserId(String keycloakId) {

        return calendarUsersApi.resolveInternalUserId(keycloakId)
                .mapNotNull(ResponseEntity::getBody)
                .onErrorResume(e -> {
                    log.error("Une erreur s'est produite lors de la récupération de l'identifiant interne de l'utilisateur : {}", e.getMessage());
                    return Mono.error(e);
                });
    }
}
