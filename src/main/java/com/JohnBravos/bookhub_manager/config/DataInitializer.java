package com.JohnBravos.bookhub_manager.config;

import com.JohnBravos.bookhub_manager.core.enums.UserRole;
import com.JohnBravos.bookhub_manager.model.User;
import com.JohnBravos.bookhub_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    @Value("${app.admin.email:admin@bookhub.com}")
    private String adminEmail;

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                User admin = new User();
                admin.setUsername(adminUsername);
                admin.setEmail(adminEmail);
                admin.setFirstName("John");
                admin.setLastName("Bravos");
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRole(UserRole.ADMIN);
                userRepository.save(admin);
                log.info("Initialized database with default admin user: {}", admin.getUsername());
            } else {
                log.info("Database already initialized with {} users.", userRepository.count());
            }
        };
    }
}
