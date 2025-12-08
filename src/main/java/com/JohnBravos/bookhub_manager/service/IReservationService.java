package com.JohnBravos.bookhub_manager.service;

import com.JohnBravos.bookhub_manager.core.enums.ReservationStatus;
import com.JohnBravos.bookhub_manager.dto.Request.CreateReservationRequest;
import com.JohnBravos.bookhub_manager.dto.Request.UpdateReservationRequest;
import com.JohnBravos.bookhub_manager.dto.Response.ReservationResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IReservationService {

    // CREATE
    ReservationResponse createReservation(CreateReservationRequest request);

    // READ
    ReservationResponse getReservationById(Long id);
    Page<ReservationResponse> getAllReservations(int page, int size, String sort, String status);
    Page<ReservationResponse> getReservationsByUser(Long userId, int page, int size, String sort);
    List<ReservationResponse> getReservationsByBook(Long bookId);
    Page<ReservationResponse> getReservationsByStatus(ReservationStatus status, int page, int size, String sort);
    Page<ReservationResponse> getActiveReservations(int page, int size, String sort);
    List<ReservationResponse> getExpiredReservations();
    List<ReservationResponse> getReservationsExpiringSoon();
    List<ReservationResponse> getReservationQueueForBook(Long bookId);

    // UPDATE
    ReservationResponse updateReservation(Long reservationId, UpdateReservationRequest request);
    ReservationResponse cancelReservation(Long reservationId);
    ReservationResponse fulfillReservation(Long reservationId);

    // DELETE
    void deleteReservation(Long reservationId);

    // UTILITY
    boolean reservationExists(Long reservationId);
    boolean hasActiveReservation(Long userId, Long bookId);
    int getQueuePosition(Long reservationId);
    int getActiveReservationsCountByUser(Long userId);
}
