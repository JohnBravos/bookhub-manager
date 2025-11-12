package com.JohnBravos.bookhub_manager.security;


import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Το μυστικό κλειδί (Θα μπει στο application.properties)
    private final String secret;

    // Πόσο καιρό ισχύει το token (24h)
    private final long expiration;

    // Δημιουργία μυστικού κλειδιού από το secret string
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expiration) {
        this.secret = secret;
        this.expiration = expiration;
        System.out.println("✅ JwtUtil initialized with expiration: " + expiration);
    }

    // Μέθοδος 1: ΔΗΜΙΟΥΡΓΙΑ ΤΟΚΕΝ
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        // Προσθήκη επιπλέον πληροφοριών στο token
        if (userDetails instanceof CustomUserDetails customUserDetails) {
            claims.put("userId", customUserDetails.getUser().getId());
            claims.put("role", customUserDetails.getUser().getRole().name());
        }

        String subject = userDetails.getUsername();

        return createToken(claims, subject);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims) // Τα δεδομένα του χρήστη
                .setSubject(subject) // Το username
                .setIssuedAt(new Date(System.currentTimeMillis())) // Πότε δημιουργήθηκε
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // Πότε λήγει
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Υπογραφή με το μυστικό κλειδί
                .compact(); // Δημιουργία token string
    }

    // Μέθοδος 2: Έλεγχος ΤΟΚΕΝ
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Μέθοδος 3: ΕΞΑΓΩΓΗ USERNAME ΑΠΟ TOKEN
    public  String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Γενική μέθοδος για εξαγωγή δεδομένων από token
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Μέθοδος 4: ΕΞΑΓΩΓΗ ROLE ΑΠΟ TOKEN
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    // Μέθοδος 5: ΕΞΑΓΩΓΗ USER ID ΑΠΟ TOKEN
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    // Μέθοδος 6: ΕΞΑΓΩΓΗ ΛΗΞΗΣ TOKEN
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // ΜΕΘΟΔΟΣ 7: ΕΛΕΓΧΟΣ ΑΝ ΤΟ TOKEN ΕΧΕΙ ΛΗΞΕΙ
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
