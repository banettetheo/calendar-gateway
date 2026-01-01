package com.calendar.gateway.infrastructure.filters;

import com.calendar.gateway.domain.services.IdentityTranslatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
public class IdentityTranslatorGatewayFilterFactory extends AbstractGatewayFilterFactory<IdentityTranslatorGatewayFilterFactory.Config> {

    private static final String INTERNAL_ID_HEADER = "X-Internal-User-Id";

    private final IdentityTranslatorService identityTranslatorService;
    private final ObjectMapper objectMapper;


    public IdentityTranslatorGatewayFilterFactory(IdentityTranslatorService identityTranslatorService, ObjectMapper objectMapper) {
        super(Config.class);
        this.identityTranslatorService = identityTranslatorService;
        this.objectMapper = objectMapper;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) ->
                exchange.getPrincipal()
                    .flatMap(principal -> {

                        String externalId = principal.getName();

                        if (externalId == null || externalId.isEmpty()) {
                            log.error("Impossible d'extraire l'ID externe (SUB) de l'identité.");
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        }

                        return identityTranslatorService.getInternalId(externalId)
                                .flatMap(internalId -> {
                                    ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                                            .headers(headers -> headers.remove(INTERNAL_ID_HEADER))
                                            .header(INTERNAL_ID_HEADER, String.valueOf(internalId))
                                            .build();

                                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
                                })
                                .onErrorResume(WebClientResponseException.class, ex -> {
                                    ServerHttpResponse response = exchange.getResponse();
                                    response.setStatusCode(ex.getStatusCode());

                                    if (ex.getHeaders() != null) {
                                        response.getHeaders().putAll(ex.getHeaders());
                                        response.getHeaders().remove(HttpHeaders.CONTENT_LENGTH);
                                    }

                                    byte[] responseBody = ex.getResponseBodyAsByteArray();
                                    if (responseBody != null && responseBody.length > 0) {
                                        DataBuffer buffer = response.bufferFactory().wrap(responseBody);
                                        return response.writeWith(Mono.just(buffer));
                                    } else {
                                        return response.setComplete();
                                    }
                                });

                    });
    }

    public static class Config {
        // ... pas de configuration spécifique pour l'instant
    }
}
