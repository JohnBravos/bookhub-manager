package com.JohnBravos.bookhub_manager.service;

import com.JohnBravos.bookhub_manager.core.enums.ReservationStatus;
import com.JohnBravos.bookhub_manager.dto.Request.CreateReservationRequest;
import com.JohnBravos.bookhub_manager.dto.Request.UpdateReservationRequest;
import com.JohnBravos.bookhub_manager.dto.Response.ReservationResponse;

import java.util.List;

public interface IReservationService {

    // CREATE
    ReservationResponse createReservation(CreateReservationRequest request);

    // READ
    ReservationResponse getReservationById(Long id);
    List<ReservationResponse> getAllReservations();
    List<ReservationResponse> getReservationsByUser(Long userId);
    List<ReservationResponse> getReservationsByBook(Long bookId);
    List<ReservationResponse> getReservationsByStatus(ReservationStatus status);
    List<ReservationResponse> getActiveReservations();
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
