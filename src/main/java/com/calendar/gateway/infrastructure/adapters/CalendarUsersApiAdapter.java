package com.calendar.gateway.infrastructure.adapters;

import com.calendar.gateway.domain.ports.CalendarUsersApiPort;
import com.calendar.gateway.exception.TechnicalErrorCode;
import com.calendar.gateway.exception.TechnicalException;
import com.calendar.gateway.infrastructure.api.CalendarUsersApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CalendarUsersApiAdapter implements CalendarUsersApiPort {

    private final CalendarUsersApi calendarUsersApi;

    public CalendarUsersApiAdapter(CalendarUsersApi calendarUsersApi) {
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
