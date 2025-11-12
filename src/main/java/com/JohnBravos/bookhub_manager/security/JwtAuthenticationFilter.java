package com.JohnBravos.bookhub_manager.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException,
            IOException {

        String path = request.getServletPath();

        if (path.startsWith("/api/auth/")) {
            // ✅ Μην ελέγχεις token στα public endpoints
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // ΒΗΜΑ 1: ΕΞΑΓΩΓΗ ΤΟΚΕΝ ΑΠΟ ΤΟ HEADER
            log.info("---- JWT Filter triggered for path: {} ----", request.getServletPath());

            final String authHeader = request.getHeader("Authorization");
            log.info("Authorization header: {}", authHeader);

            String jwtToken = null;
            String username = null;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwtToken = authHeader.substring(7);  // Αφαιρούμε το "Bearer "
                log.debug("JWT Token extracted: {}", jwtToken != null ?
                        jwtToken.substring(0, Math.min(jwtToken.length(), 20)) + "..." : "null");

                // ΒΗΜΑ 2: ΕΞΑΓΩΓΗ USERNAME ΑΠΟ TOKEN
                username = jwtUtil.extractUsername(jwtToken);
                log.debug("Username extracted from token: {}", username);

            }

            // ΒΗΜΑ 3: ΕΛΕΓΧΟΣ ΑΝ Ο ΧΡΗΣΤΗΣ ΔΕΝ ΕΙΝΑΙ ΗΔΗ ΣΥΝΔΕΔΕΜΕΝΟΣ
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    // ΒΗΜΑ 4: ΦΟΡΤΩΣΗ ΣΤΟΙΧΕΙΩΝ ΧΡΗΣΤΗ ΑΠΟ ΒΑΣΗ
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    log.debug("User details loader for: {}", username);
                    log.info("Extracted username from token: {}", username);


                    // ΒΗΜΑ 5: ΕΛΕΓΧΟΣ ΕΓΚΥΡΟΤΗΤΑΣ ΤΟΚΕΝ
                    if (jwtUtil.validateToken(jwtToken, userDetails)) {
                        log.debug("Token validated successfully for user: {}", username);

                        // ΒΗΜΑ 6: ΔΗΜΙΟΥΡΓΙΑ AUTHENTICATION OBJECT
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );
                        // ΒΗΜΑ 7: ΠΡΟΣΘΗΚΗ ΛΕΠΤΟΜΕΡΕΙΩΝ ΑΙΤΗΣΗΣ
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // ΒΗΜΑ 8: ΟΡΙΣΜΟΣ AUTHENTICATION ΣΤΟ SECURITY CONTEXT
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        log.info("Authentication set in SecurityContext for user: {}", username);
                    } else {
                        log.warn("Invalid JWT token for user: {}", username);
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                    }
                }

                // ΒΗΜΑ 9: ΣΥΝΕΧΙΣΗ ΣΤΟ ΕΠΟΜΕΝΟ FILTER

        } catch (Exception e) {
            log.error("Error processing JWT authentication: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
