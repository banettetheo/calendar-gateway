package com.calendar.gateway.infrastructure.filters;

import com.calendar.gateway.domain.services.IdentityTranslatorService;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class IdentityTranslatorGatewayFilterFactory extends AbstractGatewayFilterFactory<IdentityTranslatorGatewayFilterFactory.Config> {

    private static final String INTERNAL_ID_HEADER = "X-Internal-User-Id";

    private final IdentityTranslatorService identityTranslatorService;

    public IdentityTranslatorGatewayFilterFactory(IdentityTranslatorService identityTranslatorService) {
        super(Config.class);
        this.identityTranslatorService = identityTranslatorService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) ->
                exchange.getPrincipal()
                    .flatMap(principal -> {

                        String externalId = principal.getName();

                        if (externalId == null || externalId.isEmpty()) {
                            return this.onError(exchange, "Impossible d'extraire l'ID externe (SUB) de l'identité.", HttpStatus.UNAUTHORIZED);
                        }

                        return identityTranslatorService.getInternalId(externalId)
                                .flatMap(internalId -> {
                                    ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                                            .headers(headers -> headers.remove(INTERNAL_ID_HEADER))
                                            .header(INTERNAL_ID_HEADER, String.valueOf(internalId))
                                            .build();

                                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
                                })
                                .onErrorResume(e -> this.onError(exchange, "Utilisateur non trouvé ou erreur de service : " + e.getMessage(), HttpStatus.FORBIDDEN));
                    });
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }

    public static class Config {
        // ... pas de configuration spécifique pour l'instant
    }
}
