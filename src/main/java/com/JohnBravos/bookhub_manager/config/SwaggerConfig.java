package com.JohnBravos.bookhub_manager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
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
                                .name("John Bravos")
                                .email("john.bravos@example.com")));
    }
}
