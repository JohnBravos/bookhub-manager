package com.JohnBravos.bookhub_manager.security;

import com.JohnBravos.bookhub_manager.model.User;
import com.JohnBravos.bookhub_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Ψάχνουμε τον user από τη βάση με username ή email
        User user = userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException(
                                "User not found with username or email: " + username
                        )));

        // Μετάφραση του User σε UserDetails (αυτό καταλαβαίνει το Spring Security)
        return new CustomUserDetails(user);
    }
}
