package com.JohnBravos.bookhub_manager.repository;

import com.JohnBravos.bookhub_manager.core.enums.UserRole;
import com.JohnBravos.bookhub_manager.core.enums.UserStatus;
import com.JohnBravos.bookhub_manager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // User by email
    Optional<User> findByEmail(String email);

    // User by username
    Optional<User> findByUsername(String username);

    // Users by role
    List<User> findByRole(UserRole role);

    // Users by status
    List<User> findByStatus(UserStatus status);

    // Check if user exists by email
    boolean existsByEmail(String email);

    // Check if user exists by username
    boolean existsByUsername(String username);

    // Users by first name and last name
    List<User> findByFirstNameAndLastName(String FirstName, String LastName);

    // Βρες users με active loans (custom query)
    @Query("SELECT DISTINCT u FROM User u JOIN u.loans l WHERE l.status = 'ACTIVE'")
    List<User> findUsersWithActiveLoans();

    // Βρες users by name (search)
    @Query("SELECT u FROM User u WHERE u.firstName LIKE %:name% OR u.lastName LIKE %:name%")
    List<User> findByNameContaining(@Param("name") String name);

    // Count users by role
    long countByRole(UserRole role);

    // Count users by status
    long countByStatus(UserStatus status);
}
