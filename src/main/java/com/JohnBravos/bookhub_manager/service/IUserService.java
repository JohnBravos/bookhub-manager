package com.JohnBravos.bookhub_manager.service;

import com.JohnBravos.bookhub_manager.core.enums.UserRole;
import com.JohnBravos.bookhub_manager.core.enums.UserStatus;
import com.JohnBravos.bookhub_manager.dto.Request.CreateUserRequest;
import com.JohnBravos.bookhub_manager.dto.Request.UpdateUserRequest;
import com.JohnBravos.bookhub_manager.dto.Response.UserProfileResponse;
import com.JohnBravos.bookhub_manager.dto.Response.UserResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IUserService {

    // CREATE
    UserResponse registerUser(CreateUserRequest request); // For public registration
    UserResponse createUser(CreateUserRequest request); // For admin user creation

    // READ
    UserResponse getUserById(Long id);
    UserProfileResponse getUserProfile(Long userId);
    UserProfileResponse getCurrentUserProfile();
    List<UserResponse> getAllUsers();
    Page<UserResponse> getAllUsers(int page, int size, String sort);
    List<UserResponse> getUsersByRole(UserRole role);
    List<UserResponse> searchUsersByName(String name);

    // UPDATE
    UserResponse updateUser(Long userId, UpdateUserRequest request);
    UserProfileResponse updateCurrentUserProfile(UpdateUserRequest request);
    UserResponse updateUserStatus(Long userId, UserStatus newStatus);
    UserResponse updateUserRole(Long userId, UserRole newRole);

    // DELETE
    void deleteUser(Long userId);

    // UTILITY
    boolean userExists(Long userId);
    boolean isEmailAvailable(String email);
    boolean isUsernameAvailable(String username);

    // STATISTICS
    Object getUserStatistics();
}
