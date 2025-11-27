package com.JohnBravos.bookhub_manager.service.impl;

import com.JohnBravos.bookhub_manager.core.enums.UserRole;
import com.JohnBravos.bookhub_manager.core.enums.UserStatus;
import com.JohnBravos.bookhub_manager.core.exceptions.custom.DuplicateEmailException;
import com.JohnBravos.bookhub_manager.core.exceptions.custom.DuplicateUsernameException;
import com.JohnBravos.bookhub_manager.core.exceptions.custom.UserNotFoundException;
import com.JohnBravos.bookhub_manager.dto.Request.CreateUserRequest;
import com.JohnBravos.bookhub_manager.dto.Request.UpdateUserRequest;
import com.JohnBravos.bookhub_manager.dto.Response.UserProfileResponse;
import com.JohnBravos.bookhub_manager.dto.Response.UserResponse;
import com.JohnBravos.bookhub_manager.mapper.UserMapper;
import com.JohnBravos.bookhub_manager.model.User;
import com.JohnBravos.bookhub_manager.repository.UserRepository;
import com.JohnBravos.bookhub_manager.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse registerUser(CreateUserRequest request) {
        log.info("Attempting to register user with email: {}", request.email());

        // Validation
        if (!isEmailAvailable(request.email())) {
            throw new DuplicateEmailException(request.email());
        }

        if (!isUsernameAvailable(request.username())) {
            throw new DuplicateUsernameException(request.username());
        }

        // Create user entity
        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .phoneNumber(request.phoneNumber())
                .build();

        user.setRole(UserRole.MEMBER);
        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Admin creating user: {}", request.email());

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateUsernameException(request.username());
        }

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .phoneNumber(request.phoneNumber())
                .role(UserRole.MEMBER) // Default role
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(Long id) {
        log.debug("Fetching user by ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toResponse(user);
    }

    @Override
    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return UserProfileResponse.builder()
                .user(userMapper.toResponse(user))
                .activeLoansCount(0)
                .totalLoansCount(0)
                .totalReservationsCount(0)
                .booksReadCount(0)
                .favoriteGenre("Unknown")
                .averageRating(0.0)
                .build();
    }

    @Override
    public UserProfileResponse getCurrentUserProfile() {
        // Βρίσκω τον τρέχοντα χρήστη από το Security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        log.info("Current auth principal name = {}", email);

        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return userMapper.toProfileResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        log.debug("Fetching all users");
        return userMapper.toResponseList(userRepository.findAll());
    }

    @Override
    public Page<UserResponse> getAllUsers(int page, int size, String sort) {
        String[] sortParts = sort.split(",");
        String sortField = sortParts[0];
        Sort.Direction direction = Sort.Direction.ASC;

        if (sortParts.length > 1 && sortParts[1].equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        Page<User> users = userRepository.findAll(pageable);

        return users.map(userMapper::toResponse);
    }

    @Override
    public List<UserResponse> getUsersByRole(UserRole role) {
        log.debug("Fetching users by role: {}", role);
        return userMapper.toResponseList(userRepository.findByRole(role));
    }

    public List<UserResponse> searchUsersByName(String name) {
        log.debug("Searching users by name: {}", name);
        return  userMapper.toResponseList(userRepository.findByNameContaining(name));
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        log.info("Updating user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        userMapper.updateEntity(request, user);

        if (request.role() != null && !request.role().isEmpty()) {
            try {
                user.setRole(UserRole.valueOf(request.role()));
                log.info("User role updated to: {}", request.role());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid role provided: {}", request.role());
            }
        }

        User updatedUser = userRepository.save(user);

        log.info("User updated successfully with ID: {}", userId);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    public UserProfileResponse updateCurrentUserProfile(UpdateUserRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        userMapper.updateEntity(request, user);
        User updatedUser = userRepository.save(user);

        return userMapper.toProfileResponse(updatedUser); // ✅ Και εδώ!
    }

    @Override
    @Transactional
    public UserResponse updateUserStatus(Long userId, UserStatus newStatus) {
        log.info("Updating status for user ID: {} to {}", userId, newStatus);
        User user = userRepository.findById(userId)
                .orElseThrow(() ->new UserNotFoundException(userId));

        user.setStatus(newStatus);
        User updatedUser = userRepository.save(user);

        log.info("User status updated successfully for user ID: {}", userId);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    public UserResponse updateUserRole(Long userId, UserRole newRole) {
        log.info("Updating role for user ID: {} to {}", userId, newRole);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setRole(newRole);
        User updatedUser = userRepository.save(user);

        log.info("User role updated successfully for user ID: {}", userId);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.info("Deleting user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        userRepository.delete(user);
        log.info("User deleted successfully with ID: {}", userId);
    }

    @Override
    public boolean userExists(Long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    @Override
    public Object getUserStatistics() {
        // Βασικά στατιστικά
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByStatus(UserStatus.ACTIVE);
        long members = userRepository.countByRole(UserRole.MEMBER);
        long librarians = userRepository.countByRole(UserRole.LIBRARIAN);
        long admins = userRepository.countByRole(UserRole.ADMIN);

        return Map.of(
                "totalUsers", totalUsers,
                "activeUsers", activeUsers,
                "members", members,
                "librarians", librarians,
                "admins", admins,
                "inactiveUsers", userRepository.countByStatus(UserStatus.INACTIVE),
                "suspendedUsers", userRepository.countByStatus(UserStatus.SUSPENDED)
        );
    }
}
