package com.JohnBravos.bookhub_manager.mapper;

import com.JohnBravos.bookhub_manager.dto.Request.UpdateReservationRequest;
import com.JohnBravos.bookhub_manager.dto.Response.ReservationResponse;
import com.JohnBravos.bookhub_manager.model.Reservation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReservationMapper {

    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    public ReservationMapper (UserMapper userMapper, BookMapper bookMapper) {
        this.userMapper = userMapper;
        this.bookMapper = bookMapper;
    }

    /**
     * Μετατροπή από Reservation -> ReservationResponse
     */
    public ReservationResponse toResponse(Reservation reservation) {
        if (reservation == null) {
            return null;
        }

        return new ReservationResponse(
                reservation.getId(),
                bookMapper.toResponse(reservation.getBook()),
                userMapper.toResponse(reservation.getUser()),
                reservation.getReservationDate(),
                reservation.getExpiryDate(),
                reservation.getStatus(),
                0 // positionInQueue - θα το υπολογίσουμε στο Service
        );
    }

    public void updateEntity(UpdateReservationRequest request, Reservation reservation) {
        if (request.bookId() != null) {
            // Θα χρειαστούμε BookRepository για να βρούμε το νέο βιβλίο
            // Αυτό θα γίνει στο Service
        }
        if (request.expiryDate() != null) {
            reservation.setExpiryDate(request.expiryDate());
        }
    }

    /**
     * Μετατροπή λίστας Reservations -> ReservationResponses
     */
    public List<ReservationResponse> toResponseList(List<Reservation> reservations) {
        if (reservations == null) {
            return List.of();
        }

        return reservations.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
