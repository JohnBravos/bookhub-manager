package com.JohnBravos.bookhub_manager.controller;

import com.JohnBravos.bookhub_manager.core.enums.UserStatus;
import com.JohnBravos.bookhub_manager.dto.Request.CreateUserRequest;
import com.JohnBravos.bookhub_manager.dto.Request.UpdateUserRequest;
import com.JohnBravos.bookhub_manager.dto.Response.ApiResponse;
import com.JohnBravos.bookhub_manager.dto.Response.ReservationResponse;
import com.JohnBravos.bookhub_manager.dto.Response.UserProfileResponse;
import com.JohnBravos.bookhub_manager.dto.Response.UserResponse;
import com.JohnBravos.bookhub_manager.model.User;
import com.JohnBravos.bookhub_manager.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    // GET USER PROFILE (Ο χρήστης βλέπει το δικό του profile)
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUserProfile() {
        log.info("Fetching current user profile");
        UserProfileResponse profile = userService.getCurrentUserProfile();
        log.info("Fetched profile: {}", profile);
        return ResponseEntity.ok(ApiResponse.success(profile, "User profile retrieved successfully"));
    }

    // UPDATE USER PROFILE (Ο χρήστης ενημερώνει δικά του στοιχεία)
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateCurrentUserProfile(
            @Valid @RequestBody UpdateUserRequest request) {
        log.info("Updating current user profile");
        UserProfileResponse updatedProfile = userService.updateCurrentUserProfile(request);
        return ResponseEntity.ok(ApiResponse.success(updatedProfile, "Profile updated successfully"));
    }

    // ADMIN: GET ALL USERS
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
         @RequestParam(defaultValue = "0") int page,
         @RequestParam(defaultValue = "10") int size,
         @RequestParam(defaultValue = "id,asc") String sort
    ) {
             return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers(page, size, sort)));
    }

    // GET USER BY ID (ADMIN/LIBRARIAN μόνο)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        log.info("Fetching user with ID: {}", id);
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user, "User retrieved successfully"));
    }

    // CREATE USER (Admin μόνο - για δημιουργία νέων χρηστών από admin)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        log.info("Admin creating new user: {}", request.email());
        UserResponse user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(user, "User created successfully"));
    }

    // UPDATE USER (Admin)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        log.info("Admin updating user with ID: {}", id);
        UserResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success(user, "User updated successfully"));
    }

    // UPDATE USER ROLE (Admin/Librarian)
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(
            @PathVariable Long id,
            @RequestParam UserStatus newStatus) {
        log.info("Updating status for user ID: {} to {}", id, newStatus);
        UserResponse user = userService.updateUserStatus(id, newStatus);
        return ResponseEntity.ok(ApiResponse.success(user, "User status updated successfully"));
    }

    // 🗑️ DELETE USER (Admin μόνο)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        log.info("Admin deleting user with ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
    }

    // 📈 GET USERS STATISTICS (Admin μόνο)
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> getUserStatistics() {
        log.info("Admin fetching user statistics");
        Object stats = userService.getUserStatistics();
        return ResponseEntity.ok(ApiResponse.success(stats, "User statistics retrieved successfully"));
    }
}
