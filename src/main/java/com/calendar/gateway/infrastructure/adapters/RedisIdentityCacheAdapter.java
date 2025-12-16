package com.calendar.gateway.infrastructure.adapters;

import com.calendar.gateway.domain.ports.RedisIdentityCachePort;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class RedisIdentityCacheAdapter implements RedisIdentityCachePort {

    private final ReactiveRedisTemplate<String, Long> reactiveRedisTemplate;

    private static final Duration TTL = Duration.ofHours(1);

    public RedisIdentityCacheAdapter(ReactiveRedisTemplate<String, Long> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    @Override
    public Mono<Long> findByExternalId(String keycloakId) {
        return reactiveRedisTemplate.opsForValue().get(keycloakId);
    }

    @Override
    public Mono<Void> saveMapping(String keycloakId, Long internalId) {
        return reactiveRedisTemplate.opsForValue()
                .set(keycloakId, internalId, TTL)
                .then();
    }
}
