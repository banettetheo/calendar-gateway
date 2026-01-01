package com.calendar.gateway;

import com.calendar.gateway.infrastructure.filters.IdentityTranslatorGatewayFilterFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
    public RouteLocator configureRoute(RouteLocatorBuilder builder, IdentityTranslatorGatewayFilterFactory identityTranslatorFilterFactory) {
        return builder.routes()
                .route("calendar-core-api", r -> r.path("/api/v1/core-service/**")
                        .filters(f -> f
                                .filter(identityTranslatorFilterFactory.apply(new IdentityTranslatorGatewayFilterFactory.Config()))
                                .stripPrefix(2)
                                .retry(3)
                        )
                        .uri("http://localhost:8082")
                )
                .route("calendar-social-api", r -> r
                        .path("/api/v1/social-service/**")
                        .filters(f -> f
                                .filter(identityTranslatorFilterFactory.apply(new IdentityTranslatorGatewayFilterFactory.Config()))
                                .stripPrefix(2)
                                .retry(3)
                        )
                        .uri("http://localhost:8083")
                )
                .build();
    }
}
