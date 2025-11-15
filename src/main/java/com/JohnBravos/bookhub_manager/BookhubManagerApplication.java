package com.JohnBravos.bookhub_manager;

import com.JohnBravos.bookhub_manager.repository.UserRepository;
import com.JohnBravos.bookhub_manager.security.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class BookhubManagerApplication {

	private final AuthenticationService authenticationService;
	private final UserRepository userRepository;

	public static void main(String[] args)
	{

		SpringApplication.run(BookhubManagerApplication.class, args);
	}

	@Bean
	public CommandLineRunner appStartupMessage() {
		return args -> {
			log.info("\n\n🚀 BookHub Manager application started successfully!");
			log.info("✅ All systems operational. Ready to receive requests.\n");
		};
	}

}
