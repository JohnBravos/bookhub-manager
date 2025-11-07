package com.JohnBravos.bookhub_manager.security;

import com.JohnBravos.bookhub_manager.model.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;


@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        /** Μετατρέπουμε το UserRole (MEMBER/LIBRARIAN/ADMIN) σε Spring
         *  Security authorities
         */
        // "ROLE_MEMBER", "ROLE_LIBRARIAN", "ROLE_ADMIN"
        if (user.getRole() != null) {
            return Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
            );
        }
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // Ο κρυπτογραφημένος κωδικός
    }

    @Override
    public String getUsername() {
        return user.getUsername(); // Το username για login
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Ο λογαριασμός δεν έχει λήξει
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getStatus().name().equals("ACTIVE"); // Μόνο active users
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Τα credentials δεν έχουν λήξει
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus().name().equals("ACTIVE"); // Μόνο enabled users
    }

    public User getUser() {
        return user;
    }
}
