package com.JohnBravos.bookhub_manager.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
                                .name("IOANNIS BRAVOS")
                                .email("bravosgiannis@gmail.com")))
                .addServersItem(new Server()
                        .url("http://localhost:8080/api")
                        .description("Local Development Server"))
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Local Server Root"))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token authentication. Get token by login endpoint.")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}
