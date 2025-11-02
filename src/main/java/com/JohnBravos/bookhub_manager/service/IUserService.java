package com.JohnBravos.bookhub_manager.service;

import com.JohnBravos.bookhub_manager.core.enums.UserRole;
import com.JohnBravos.bookhub_manager.core.enums.UserStatus;
import com.JohnBravos.bookhub_manager.dto.Request.CreateUserRequest;
import com.JohnBravos.bookhub_manager.dto.Request.UpdateUserRequest;
import com.JohnBravos.bookhub_manager.dto.Response.UserProfileResponse;
import com.JohnBravos.bookhub_manager.dto.Response.UserResponse;

import java.util.List;

public interface IUserService {

    // CREATE
    UserResponse registerUser(CreateUserRequest request);

    // READ
    UserResponse getUserById(Long id);
    UserProfileResponse getUserProfile(Long userId);
    List<UserResponse> getAllUsers();
    List<UserResponse> getUsersByRole(UserRole role);
    List<UserResponse> searchUsersByName(String name);

    // UPDATE
    UserResponse updateUser(Long userId, UpdateUserRequest request);
    UserResponse updateUserStatus(Long userId, UserStatus newStatus);
    UserResponse updateUserRole(Long userId, UserRole newRole);

    // DELETE
    void deleteUser(Long userId);

    // UTILITY
    boolean userExists(Long userId);
    boolean isEmailAvailable(String email);
    boolean isUsernameAvailable(String username);
}
