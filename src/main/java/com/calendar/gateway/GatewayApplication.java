package com.calendar.gateway;

import com.calendar.gateway.infrastructure.filters.IdentityTranslatorGatewayFilterFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayApplication {

    private final IdentityTranslatorGatewayFilterFactory identityTranslatorFilterFactory;

    public GatewayApplication(IdentityTranslatorGatewayFilterFactory identityTranslatorFilterFactory) {
        this.identityTranslatorFilterFactory = identityTranslatorFilterFactory;
    }

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {

        return builder.routes()
                .route("calendar-users-api", r -> r
                        .path("/api/v1/users/**", "/api/v1/profile/**")
                        .filters(f -> f
                                .filter(identityTranslatorFilterFactory.apply(new IdentityTranslatorGatewayFilterFactory.Config()))
                                .stripPrefix(2)
                        )
                        .uri("http://localhost:8082")
                )
                .route("calendar-social-api", r -> r
                        .path("/api/v1/social-service/**")
                        .filters(f -> f
                                .filter(identityTranslatorFilterFactory.apply(new IdentityTranslatorGatewayFilterFactory.Config()))
                                .stripPrefix(2)
                        )
                        .uri("http://localhost:8083")
                )
                .build();
    }
}
