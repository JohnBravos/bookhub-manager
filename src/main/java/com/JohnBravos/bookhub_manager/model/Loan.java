package com.JohnBravos.bookhub_manager.model;


import com.JohnBravos.bookhub_manager.core.enums.LoanStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Builder
@Table(name="loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ποιο βιβλίο δανείζεται
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    // Ποιος χρήστης το δανείζεται
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime loanDate;

    @Column(nullable = false)
    private LocalDateTime dueDate;      // Προθεσμία επιστροφής

    private LocalDateTime returnDate;   // Actual return time

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status = LoanStatus.ACTIVE;

    // Στο Loan entity μπορούμε να προσθέσουμε:
    public boolean isOverdue() {
        return LocalDateTime.now().isAfter(dueDate) && status == LoanStatus.ACTIVE;
    }

    public boolean isReturned() {
        return returnDate != null && status == LoanStatus.RETURNED;
    }

    public void markAsReturned() {
        this.returnDate = LocalDateTime.now();
        this.status = LoanStatus.RETURNED;
        this.book.returnCopy(); // Update book availability
    }

}
