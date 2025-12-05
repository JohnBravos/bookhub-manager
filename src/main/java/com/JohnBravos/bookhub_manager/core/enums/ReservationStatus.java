package com.JohnBravos.bookhub_manager.core.enums;

public enum ReservationStatus {
    PENDING,    // Αναμονή
    ACTIVE,     // Ενεργή κράτηση
    READY,      // Έτοιμη για παραλαβή
    FULFILLED,  // Ολοκληρωμένη Κράτηση
    CANCELLED,  // Ακυρωμένη κράτηση
    EXPIRED     // Κράτηση που έληξε
}
