package com.JohnBravos.bookhub_manager.controller;

import com.JohnBravos.bookhub_manager.core.enums.UserStatus;
import com.JohnBravos.bookhub_manager.dto.Request.ChangePasswordRequest;
import com.JohnBravos.bookhub_manager.dto.Request.CreateUserRequest;
import com.JohnBravos.bookhub_manager.dto.Request.SystemSettingsRequest;
import com.JohnBravos.bookhub_manager.dto.Request.UpdateUserRequest;
import com.JohnBravos.bookhub_manager.dto.Response.*;
import com.JohnBravos.bookhub_manager.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Users", description = "User Management and Profile API")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @GetMapping("/profile")
    @Operation(summary = "Get current user profile", description = "Retrieve the authenticated user's own profile")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User profile retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUserProfile() {
        log.info("Fetching current user profile");
        UserProfileResponse profile = userService.getCurrentUserProfile();
        log.info("Fetched profile: {}", profile);
        return ResponseEntity.ok(ApiResponse.success(profile, "User profile retrieved successfully"));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update current user profile", description = "Update the authenticated user's profile information")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateCurrentUserProfile(
            @Parameter(description = "User profile update request") @Valid @RequestBody UpdateUserRequest request) {
        log.info("Updating current user profile");
        UserProfileResponse updatedProfile = userService.updateCurrentUserProfile(request);
        return ResponseEntity.ok(ApiResponse.success(updatedProfile, "Profile updated successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Get all users", description = "Retrieve all users with pagination (ADMIN or LIBRARIAN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort format (e.g., id,asc)") @RequestParam(defaultValue = "id,asc") String sort) {
        return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers(page, size, sort)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user details (ADMIN or LIBRARIAN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @Parameter(description = "User ID") @PathVariable Long id) {
        log.info("Fetching user with ID: {}", id);
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user, "User retrieved successfully"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new user", description = "Create a new user account (ADMIN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data or user already exists"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Parameter(description = "User creation request") @Valid @RequestBody CreateUserRequest request) {
        log.info("Admin creating new user: {}", request.email());
        UserResponse user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(user, "User created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user", description = "Update user information (ADMIN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Parameter(description = "User update request") @Valid @RequestBody UpdateUserRequest request) {
        log.info("Admin updating user with ID: {}", id);
        UserResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success(user, "User updated successfully"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Update user status", description = "Update user account status (ADMIN or LIBRARIAN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User status updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Parameter(description = "New user status (ACTIVE, INACTIVE, SUSPENDED, BANNED)") @RequestParam UserStatus newStatus) {
        log.info("Updating status for user ID: {} to {}", id, newStatus);
        UserResponse user = userService.updateUserStatus(id, newStatus);
        return ResponseEntity.ok(ApiResponse.success(user, "User status updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Delete a user account (ADMIN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @Parameter(description = "User ID") @PathVariable Long id) {
        log.info("Admin deleting user with ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Get user statistics", description = "Retrieve user statistics (ADMIN or LIBRARIAN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User statistics retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<Object>> getUserStatistics() {
        log.info("Admin and Librarian fetching user statistics");
        Object stats = userService.getUserStatistics();
        return ResponseEntity.ok(ApiResponse.success(stats, "User statistics retrieved successfully"));
    }

    @GetMapping("/admin/settings")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get system settings", description = "Retrieve system configuration settings (ADMIN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "System settings retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
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

    @PostMapping("/admin/settings")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update system settings", description = "Update system configuration settings (ADMIN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "System settings updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<SystemSettingsResponse>> updateSystemSettings(
            @Parameter(description = "System settings update request") @Valid @RequestBody SystemSettingsRequest request) {
        log.info("Admin updating system settings");
        SystemSettingsResponse settings = new SystemSettingsResponse(
                request.getMaxLoansPerMember(),
                request.getLoanPeriodDays(),
                request.getMaxReservationsPerBook(),
                request.getLateFeePerDay(),
                request.isRenewalAllowed(),
                request.getMaxRenewals()
        );
        return ResponseEntity.ok(ApiResponse.success(settings, "System settings updated successfully"));
    }

    @GetMapping("/stats/system")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Operation(summary = "Get system statistics", description = "Retrieve comprehensive system statistics (ADMIN or LIBRARIAN only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "System statistics retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<SystemStatsResponse>> getSystemStatistics() {
        log.info("Admin/Librarian fetching system statistics");
        SystemStatsResponse stats = userService.getSystemStatistics();
        return ResponseEntity.ok(ApiResponse.success(stats, "System statistics retrieved successfully"));
    }

    @GetMapping("/{userId}/statistics")
    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Get user statistics by ID", description = "Retrieve statistics for a specific user (MEMBER, LIBRARIAN, or ADMIN)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User statistics retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<UserStatisticsResponse>> getUserStatistics(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        UserStatisticsResponse stats = userService.getUserStatistics(userId);
        return ResponseEntity.ok(ApiResponse.success(stats, "User statistics retrieved successfully"));
    }

    @PostMapping("/change-password")
    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN', 'ADMIN')")
    @Operation(summary = "Change password", description = "Change the authenticated user's password (authenticated users only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Parameter(description = "Change password request") @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePasswordForCurrentUser(request.newPassword());
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
    }
}
