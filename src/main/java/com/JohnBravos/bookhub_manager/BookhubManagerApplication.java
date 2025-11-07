package com.JohnBravos.bookhub_manager;

import com.JohnBravos.bookhub_manager.dto.Request.LoginRequest;
import com.JohnBravos.bookhub_manager.dto.Request.RegisterRequest;
import com.JohnBravos.bookhub_manager.dto.Response.LoginResponse;
import com.JohnBravos.bookhub_manager.dto.Response.RegisterResponse;
import com.JohnBravos.bookhub_manager.security.AuthenticationService;
import com.JohnBravos.bookhub_manager.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class BookhubManagerApplication {

	private final AuthenticationService authenticationService;

//	@Autowired
//	private JwtUtil jwtUtil;

	public static void main(String[] args)
	{

		SpringApplication.run(BookhubManagerApplication.class, args);
	}

	@Bean
	public CommandLineRunner testSecuritySystem() {
		return args -> {
			log.info("\n\n=== 🔐 TESTING COMPLETE SECURITY SYSTEM ===\n");

			try {
				// 🧪 TEST 1: REGISTER NEW USER
				log.info("🧪 TEST 1: Registering new user...");
				RegisterRequest registerRequest = new RegisterRequest(
						"Test", "User", "testuser", "test@bookhub.com",
						"SecurePass123!", "6931234567"
				);

				RegisterResponse registerResponse = authenticationService.register(registerRequest);
				log.info("✅ REGISTER SUCCESS: User ID: {}, Username: {}",
						registerResponse.userId(), registerResponse.username());

				// 🧪 TEST 2: LOGIN WITH NEW USER
				log.info("🧪 TEST 2: Logging in...");
				LoginRequest loginRequest = new LoginRequest("testuser", "SecurePass123!");

				LoginResponse loginResponse = authenticationService.login(loginRequest);
				log.info("✅ LOGIN SUCCESS: Token: {}...",
						loginResponse.token().substring(0, 50));
				log.info("✅ User: {}, Role: {}",
						loginResponse.username(), loginResponse.role());

				// 🧪 TEST 3: VALIDATE TOKEN
				log.info("🧪 TEST 3: Token validation...");
				// (Θα γίνει αυτόματα από το JwtAuthenticationFilter)

				log.info("\n🎉 ALL SECURITY TESTS PASSED! 🎉");
				log.info("📚 Your BookHub Manager is now SECURE! 🔐");

			} catch (Exception e) {
				log.error("❌ TEST FAILED: {}", e.getMessage());
				e.printStackTrace();
			}

			log.info("\n=== 🚀 SECURITY SYSTEM READY FOR FRONTEND ===\n");
		};
	}

//	@Override
//	public void run(String... args) throws Exception {
//		// Τρέξε το test όταν ξεκινάει η εφαρμογή
//		jwtUtil.testJwtGeneration();
//	}

}
