package com.JohnBravos.bookhub_manager;

import com.JohnBravos.bookhub_manager.core.enums.UserRole;
import com.JohnBravos.bookhub_manager.core.enums.UserStatus;
import com.JohnBravos.bookhub_manager.model.User;
import com.JohnBravos.bookhub_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class BookhubManagerApplication {

	public static void main(String[] args)
	{

		SpringApplication.run(BookhubManagerApplication.class, args);
	}

//	@Bean
//	public CommandLineRunner appStartupMessage() {
//		return args -> {
//			log.info("\n\n🚀 BookHub Manager application started successfully!");
//			log.info("✅ All systems operational. Ready to receive requests.\n");
//		};
//	}
//
//	@Bean
//	public CommandLineRunner initializeAdmin(
//			UserRepository userRepository,
//			PasswordEncoder passwordEncoder,
//			@Value("${admin.default.firstName:Admin}") String adminFirstName,
//			@Value("${admin.default.lastName:User}") String adminLastName,
//			@Value("${admin.default.username:admin}") String adminUsername,
//			@Value("${admin.default.email:admin@bookhub.com}") String adminEmail,
//			@Value("${admin.default.password:Admin@123}") String adminPassword,
//			@Value("${admin.default.phone:0000000000}") String adminPhone) {
//
//		return args -> {
//			log.info("========================================");
//			log.info("🔥 Admin Initialization is running...");
//			log.info("========================================");
//
//			long adminCount = userRepository.countByRole(UserRole.ADMIN);
//
//			if (adminCount == 0) {
//				log.info("🔧 No admin user found. Creating default admin...");
//
//				User admin = User.builder()
//						.firstName(adminFirstName)
//						.lastName(adminLastName)
//						.username(adminUsername)
//						.email(adminEmail)
//						.password(passwordEncoder.encode(adminPassword))
//						.phoneNumber(adminPhone)
//						.role(UserRole.ADMIN)
//						.status(UserStatus.ACTIVE)
//						.build();
//
//				userRepository.save(admin);
//
//				log.info("✅ Default admin user created successfully!");
//				log.info("   Username: {}", adminUsername);
//				log.info("   Email: {}", adminEmail);
//				log.warn("⚠️  IMPORTANT: Change the default password after first login!");
//			} else {
//				log.info("✅ Admin user already exists (count: {}). Skipping initialization.", adminCount);
//			}
//		};
//	}

}
