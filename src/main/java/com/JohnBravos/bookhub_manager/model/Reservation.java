package com.JohnBravos.bookhub_manager.model;

import com.JohnBravos.bookhub_manager.core.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate reservationDate;

    @Column(nullable = false)
    private LocalDate expiryDate;       // Πότε λήγει η κράτηση

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status", length = 20)
    private ReservationStatus status = ReservationStatus.ACTIVE;
}
