package com.JohnBravos.bookhub_manager.controller;

import com.JohnBravos.bookhub_manager.core.enums.UserStatus;
import com.JohnBravos.bookhub_manager.dto.Request.ChangePasswordRequest;
import com.JohnBravos.bookhub_manager.dto.Request.CreateUserRequest;
import com.JohnBravos.bookhub_manager.dto.Request.SystemSettingsRequest;
import com.JohnBravos.bookhub_manager.dto.Request.UpdateUserRequest;
import com.JohnBravos.bookhub_manager.dto.Response.*;
import com.JohnBravos.bookhub_manager.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<Object>> getUserStatistics() {
        log.info("Admin and Librarian fetching user statistics");
        Object stats = userService.getUserStatistics();
        return ResponseEntity.ok(ApiResponse.success(stats, "User statistics retrieved successfully"));
    }    
    // GET SYSTEM SETTINGS (Admin only)
    @GetMapping("/admin/settings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SystemSettingsResponse>> getSystemSettings() {
        log.info("Fetching system settings");
        SystemSettingsResponse settings = new SystemSettingsResponse(
            10,      // maxLoansPerMember
            14,      // loanPeriodDays
            5,       // maxReservationsPerBook
            1.5,     // lateFeePerDay
            true,    // renewalAllowed
            2        // maxRenewals
        );
        return ResponseEntity.ok(ApiResponse.success(settings, "System settings retrieved successfully"));
    }
    // POST SYSTEM SETTINGS (Admin only) - Update system settings
    @PostMapping("/admin/settings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SystemSettingsResponse>> updateSystemSettings(
            @Valid @RequestBody SystemSettingsRequest request) {
        log.info("Admin updating system settings");
        SystemSettingsResponse settings = new SystemSettingsResponse(
            request.getMaxLoansPerMember(),
            request.getLoanPeriodDays(),
            request.getMaxReservationsPerBook(),
            request.getLateFeePerDay(),
            request.isRenewalAllowed(),
            request.getMaxRenewals()
        );
        // In production, you would save these to a database or config service
        return ResponseEntity.ok(ApiResponse.success(settings, "System settings updated successfully"));
    }
    // GET SYSTEM STATISTICS (Admin/Librarian)
    @GetMapping("/stats/system")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<SystemStatsResponse>> getSystemStatistics() {
        log.info("Admin/Librarian fetching system statistics");
        SystemStatsResponse stats = userService.getSystemStatistics();
        return ResponseEntity.ok(ApiResponse.success(stats, "System statistics retrieved successfully"));
    }

    // GET USER STATISTICS BY USER ID (Member/Librarian/Admin)
    @GetMapping("/{userId}/statistics")
    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<UserStatisticsResponse>> getUserStatistics(@PathVariable Long userId) {
        UserStatisticsResponse stats = userService.getUserStatistics(userId);
        return ResponseEntity.ok(ApiResponse.success(stats, "User statistics retrieved successfully"));
    }

    // CHANGE PASSWORD (Member/Librarian/Admin)
    @PostMapping("/change-password")
    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePasswordForCurrentUser(request.newPassword());
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
    }
}



