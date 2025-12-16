package com.calendar.gateway.configuration;

import com.calendar.gateway.infrastructure.api.CalendarUsersApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfig {

    @Value("${calendar-users-api.base-url}")
    private String calendarUsersApiUrl;

    @Bean
    WebClient calendarUsersApiWebClient() {
        return WebClient.builder()
                .baseUrl(calendarUsersApiUrl)
                .filter(propagateJwtFilter())
                .build();
    }

    @Bean
    HttpServiceProxyFactory httpServiceProxyFactory(@Qualifier("calendarUsersApiWebClient") WebClient webClient) {
        return HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient))
                .build();
    }

    @Bean
    public CalendarUsersApi userRestApi(HttpServiceProxyFactory httpServiceProxyFactory) {
        return httpServiceProxyFactory.createClient(CalendarUsersApi.class);
    }

    private ExchangeFilterFunction propagateJwtFilter() {
        return (request, next) ->
            ReactiveSecurityContextHolder.getContext()
                    .map(securityContext -> securityContext.getAuthentication())
                    .filter(authentication -> authentication.getPrincipal() instanceof Jwt)
                    .flatMap(authentication -> {
                        Jwt jwt = (Jwt) authentication.getPrincipal();
                        String token = jwt.getTokenValue();

                        ClientRequest filteredRequest = ClientRequest.from(request)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .build();

                        return next.exchange(filteredRequest);
                    })
                    .switchIfEmpty(Mono.defer(() -> next.exchange(request))); // Utilisation de Mono.defer pour l'exécution paresseuse
    }
}
