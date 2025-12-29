package com.calendar.gateway.configuration;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import org.springframework.boot.data.redis.autoconfigure.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {
    
    @Bean
    public ReactiveRedisTemplate<String, Long> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {

        StringRedisSerializer keySerializer = new StringRedisSerializer();

        GenericToStringSerializer<Long> valueSerializer = new GenericToStringSerializer<>(Long.class);

        RedisSerializationContext<String, Long> serializationContext = RedisSerializationContext
                .<String, Long>newSerializationContext()
                .key(keySerializer)
                .value(valueSerializer)
                .hashKey(keySerializer)
                .hashValue(valueSerializer)
                .build();

        return new ReactiveRedisTemplate<>(factory, serializationContext);
    }

    @Bean
    public LettuceClientConfigurationBuilderCustomizer lettuceCustomizer() {
        return builder -> {
            SocketOptions socketOptions = SocketOptions.builder()
                    .keepAlive(true)
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();

            ClientOptions clientOptions = ClientOptions.builder()
                    .socketOptions(socketOptions)
                    .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                    .build();

            builder.clientOptions(clientOptions);
        };
    }
}