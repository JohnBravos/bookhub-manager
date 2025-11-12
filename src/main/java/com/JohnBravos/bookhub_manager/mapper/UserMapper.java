package com.JohnBravos.bookhub_manager.mapper;

import com.JohnBravos.bookhub_manager.dto.Request.UpdateUserRequest;
import com.JohnBravos.bookhub_manager.dto.Response.UserProfileResponse;
import com.JohnBravos.bookhub_manager.dto.Response.UserResponse;
import com.JohnBravos.bookhub_manager.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );

    }

    public UserProfileResponse toProfileResponse(User user) {
        if (user == null) {
            return null;
        }

        UserResponse userResponse = toResponse(user);

        // ✅ TEMPORARY VALUES - θα τα βελτιώσουμε ΑΡΓΟΤΕΡΑ
        return new UserProfileResponse(
                userResponse,
                0,  // activeLoansCount - temporary
                0,  // totalLoansCount - temporary
                0,  // totalReservationsCount - temporary
                List.of(),  // currentLoans - temporary empty list
                List.of(),  // currentReservations - temporary empty list
                0,  // booksReadCount - temporary
                "N/A",  // favoriteGenre - temporary
                0.0  // averageRating - temporary
        );
    }

    public void updateEntity(UpdateUserRequest request, User user) {
        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }
        if (request.phoneNumber() != null) {
            user.setPhoneNumber(request.phoneNumber());
        }
    }

    public List<UserResponse> toResponseList(List<User> users) {
        if (users == null) {
            return List.of();
        }

        return users.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
