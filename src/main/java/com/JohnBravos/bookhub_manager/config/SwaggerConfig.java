package com.JohnBravos.bookhub_manager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI Configuration for BookHub Manager API
 * Access Swagger UI at: http://localhost:8080/api/swagger-ui.html
 * Access API docs at: http://localhost:8080/api/v3/api-docs
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI bookHubOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BookHub Manager API")
                        .description("Library Management System - REST API Documentation")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("John Bravos")
                                .email("john.bravos@example.com")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT Bearer token for API authentication")));
    }
}
