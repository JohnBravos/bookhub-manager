package com.JohnBravos.bookhub_manager.security;

import com.JohnBravos.bookhub_manager.core.enums.UserRole;
import com.JohnBravos.bookhub_manager.core.enums.UserStatus;
import com.JohnBravos.bookhub_manager.core.exceptions.custom.DuplicateEmailException;
import com.JohnBravos.bookhub_manager.core.exceptions.custom.DuplicateUsernameException;
import com.JohnBravos.bookhub_manager.dto.Request.LoginRequest;
import com.JohnBravos.bookhub_manager.dto.Request.RegisterRequest;
import com.JohnBravos.bookhub_manager.dto.Response.LoginResponse;
import com.JohnBravos.bookhub_manager.dto.Response.RegisterResponse;
import com.JohnBravos.bookhub_manager.model.User;
import com.JohnBravos.bookhub_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    private final AuthenticationManager authenticationManager;

    // ΜΕΘΟΔΟΣ 1: ΕΓΓΡΑΦΗ ΝΕΟΥ ΧΡΗΣΤΗ
    public RegisterResponse register(RegisterRequest request) {
        log.info("Attempting to register new user: {}", request.email());

        // ΒΗΜΑ 1: ΕΛΕΓΧΟΣ ΑΝ ΥΠΑΡΧΕΙ ΗΔΗ ΧΡΗΣΤΗΣ
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }

        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateUsernameException(request.username());
        }

        // ΒΗΜΑ 2: ΔΗΜΙΟΥΡΓΙΑ ΝΕΟΥ ΧΡΗΣΤΗ
        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .phoneNumber(request.phoneNumber())
                .role(UserRole.MEMBER)
                .status(UserStatus.ACTIVE)
                .build();

        // ΒΗΜΑ 3: ΑΠΟΘΗΚΕΥΣΗ ΣΤΗ ΒΑΣΗ
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        // ΒΗΜΑ 4: ΕΠΙΣΤΡΟΦΗ ΑΠΑΝΤΗΣΗΣ
        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                "User registered successfully"
        );
    }

    // ΜΕΘΟΔΟΣ 2: ΕΙΣΟΔΟΣ ΧΡΗΣΤΗ
    public LoginResponse login(LoginRequest request) {
        log.info("Attempting to login user: {}", request.username());

        // ΒΗΜΑ 1: ΕΛΕΓΧΟΣ ΤΑΥΤΟΤΗΤΑΣ ΜΕ ΤΟ SPRING SECURITY
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        // ΒΗΜΑ 2: ΟΡΙΣΜΟΣ ΣΤΟ SECURITY CONTEXT
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("Authentication successful for user: {}", request.username());

        // ΒΗΜΑ 3: ΔΗΜΙΟΥΡΓΙΑ JWT TOKEN
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String jwtToken = jwtUtil.generateToken(userDetails);
        log.debug("Jwt token generated for user: {}", request.username());

        // ΒΗΜΑ 4: ΕΠΙΣΤΡΟΦΗ ΑΠΑΝΤΗΣΗΣ ΜΕ ΤΟΚΕΝ
        return new LoginResponse(
                jwtToken,
                userDetails.getUser().getId(),
                userDetails.getUsername(),
                userDetails.getUser().getEmail(),
                userDetails.getUser().getRole().name(),
                "Login successful"
        );
        
    }
}
