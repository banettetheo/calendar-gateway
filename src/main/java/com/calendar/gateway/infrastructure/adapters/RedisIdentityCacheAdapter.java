package com.calendar.gateway.infrastructure.adapters;

import com.calendar.gateway.domain.ports.RedisIdentityCachePort;
import com.calendar.gateway.exception.TechnicalErrorCode;
import com.calendar.gateway.exception.TechnicalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Component
public class RedisIdentityCacheAdapter implements RedisIdentityCachePort {

    private final ReactiveRedisTemplate<String, Long> reactiveRedisTemplate;

    private static final Duration TTL = Duration.ofHours(1);

    public RedisIdentityCacheAdapter(ReactiveRedisTemplate<String, Long> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    @Override
    public Mono<Long> findByExternalId(String keycloakId) {
        return reactiveRedisTemplate.opsForValue().get(keycloakId)
                .onErrorMap(e -> {
                    log.error("Une erreur est survenue lors de la récupération du cache : {}", e.getMessage());
                    return new TechnicalException(TechnicalErrorCode.REDIS_ERROR);
                });
    }

    @Override
    public Mono<Void> saveMapping(String keycloakId, Long internalId) {
        return reactiveRedisTemplate.opsForValue()
                .set(keycloakId, internalId, TTL)
                .onErrorMap(e -> {
                    log.error("Une erreur est survenue lors de l'enregistrement de la mise en cache : {}", e.getMessage());
                    return new TechnicalException(TechnicalErrorCode.REDIS_ERROR);
                })
                .then();
    }
}
