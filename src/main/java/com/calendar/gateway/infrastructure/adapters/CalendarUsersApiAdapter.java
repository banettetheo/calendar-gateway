package com.calendar.gateway.infrastructure.adapters;

import com.calendar.gateway.domain.ports.CalendarUsersApiPort;
import com.calendar.gateway.infrastructure.api.CalendarUsersApi;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CalendarUsersApiAdapter implements CalendarUsersApiPort {

    private final CalendarUsersApi calendarUsersApi;

    public CalendarUsersApiAdapter(CalendarUsersApi calendarUsersApi) {
        this.calendarUsersApi = calendarUsersApi;
    }

    @Override
    public Mono<Long> fetchInternalUserId(String keycloakId) {

        return calendarUsersApi.readProfile(keycloakId)
                .map(response -> {
                    assert response.getBody() != null;
                    return response.getBody().id();
                })
                .doOnError(throwable -> throwable.printStackTrace());
    }
}
