//package com.JohnBravos.bookhub_manager.config;
//
//import com.JohnBravos.bookhub_manager.core.enums.UserRole;
//import com.JohnBravos.bookhub_manager.core.enums.UserStatus;
//import com.JohnBravos.bookhub_manager.model.User;
//import com.JohnBravos.bookhub_manager.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
///**
// * Αρχικοποίηση δεδομένων κατά την εκκίνηση της εφαρμογής.
// * Δημιουργεί έναν default admin χρήστη αν δεν υπάρχει.
// */
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class DataInitializer implements CommandLineRunner {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Value("${admin.default.firstName:Admin}")
//    private String adminFirstName;
//
//    @Value("${admin.default.lastName:User}")
//    private String adminLastName;
//
//    @Value("${admin.default.username:admin}")
//    private String adminUsername;
//
//    @Value("${admin.default.email:admin@bookhub.com}")
//    private String adminEmail;
//
//    @Value("${admin.default.password:Admin@123}")
//    private String adminPassword;
//
//    @Value("${admin.default.phone:0000000000}")
//    private String adminPhone;
//
//    @Override
//    public void run(String... args) {
//        initializeAdmin();
//    }
//
//    private void initializeAdmin() {
//        log.info("========================================");
//        log.info("🔥 Admin Initialization is running...");
//        log.info("========================================");
//
//        long adminCount = userRepository.countByRole(UserRole.ADMIN);
//
//        if (adminCount == 0) {
//            log.info("🔧 No admin user found. Creating default admin...");
//
//            User admin = User.builder()
//                    .firstName(adminFirstName)
//                    .lastName(adminLastName)
//                    .username(adminUsername)
//                    .email(adminEmail)
//                    .password(passwordEncoder.encode(adminPassword))
//                    .phoneNumber(adminPhone)
//                    .role(UserRole.ADMIN)
//                    .status(UserStatus.ACTIVE)
//                    .build();
//
//            userRepository.save(admin);
//
//            log.info("✅ Default admin user created successfully!");
//            log.info("   Username: {}", adminUsername);
//            log.info("   Email: {}", adminEmail);
//            log.warn("⚠️  IMPORTANT: Change the default password after first login!");
//        } else {
//            log.info("✅ Admin user already exists (count: {}). Skipping initialization.", adminCount);
//        }
//    }
//}
